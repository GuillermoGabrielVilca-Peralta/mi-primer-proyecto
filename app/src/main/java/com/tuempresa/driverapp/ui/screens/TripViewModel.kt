package com.tuempresa.driverapp.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.local.models.Trip
import com.tuempresa.driverapp.data.local.models.TripEvent
import com.tuempresa.driverapp.data.repository.DriverRepository
import com.tuempresa.driverapp.domain.ScoreCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TripScreenState(
    val speed: Int = 0,
    val alertMessage: String = "Iniciando...",
    val isCriticalAlert: Boolean = false,
    val currentScore: Int = 100
)

@HiltViewModel
class TripViewModel @Inject constructor(
    private val repo: DriverRepository,
    private val scoreCalculator: ScoreCalculator
): ViewModel() {

    private val _uiState = MutableStateFlow(TripScreenState())
    val uiState = _uiState.asStateFlow()

    private var tripStartTime: Long = 0L
    private var maxSpeedDuringTrip: Int = 0
    private var redLightEvaluationJob: Job? = null

    fun startTrip() {
        tripStartTime = System.currentTimeMillis()
        _uiState.update { it.copy(alertMessage = "Todo en orden") }
    }

    fun postAlert(message: String, isCritical: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                alertMessage = message,
                isCriticalAlert = isCritical
            )
        }
    }

    fun updateSpeed(newSpeed: Int) {
        // 👇 AÑADE ESTA LÍNEA 👇
        if (newSpeed > maxSpeedDuringTrip) {
            maxSpeedDuringTrip = newSpeed
        }
        _uiState.update { it.copy(speed = newSpeed) }
    }

    fun updateScore(newScore: Int) {
        val score = newScore.coerceIn(0, 100)
        _uiState.update { it.copy(currentScore = score) }

        viewModelScope.launch {

            repo.updateLiveScore(score)
        }
    }
    // Reemplaza la función finishTrip() completa en TripViewModel.kt
    fun finishTrip() {
        viewModelScope.launch {
            val endTime = System.currentTimeMillis()
            val durationSeconds = ((endTime - tripStartTime) / 1000).toInt()

            // 👇 CREACIÓN DEL OBJETO 'Trip' CORREGIDA 👇
            val newTrip = Trip(
                id = UUID.randomUUID().toString(),
                driverId = "driver_01", // Temporal: Esto debería venir del usuario logueado
                startTs = tripStartTime,
                endTs = endTime,
                timeActiveSeconds = durationSeconds,
                finalScore = _uiState.value.currentScore,
                maxSpeed = maxSpeedDuringTrip.toDouble(),
                // Los siguientes son valores simulados por ahora.
                avgSpeed = if (durationSeconds > 0) 60.0 else 0.0,
                distanceKm = if (durationSeconds > 0) (durationSeconds / 3600.0) * 60.0 else 0.0,
                isSynced = false // Por defecto, no está sincronizado
            )
            repo.saveTrip(newTrip)
            Log.d("TripViewModel", "Viaje guardado con ID: ${newTrip.id}")
        }
    }


    fun calculateScore(events: List<TripEvent>, excesoPromKmh: Double): Int {
        val finalScore = scoreCalculator.calculateFinalScore(events, excesoPromKmh)
        updateScore(finalScore)
        return finalScore
    }

    // ===== INICIO: CÓDIGO AÑADIDO PARA LA LÓGICA DEL SEMÁFORO =====

    /**
     * Se llama desde el servicio de cámara cuando se detecta un semáforo en rojo.
     */
    fun onRedLightDetected() {
        if (redLightEvaluationJob?.isActive == true) return

        val initialSpeed = _uiState.value.speed
        Log.d("TripViewModel", "Semáforo en Rojo Detectado. Velocidad inicial: $initialSpeed km/h")

        // Si ya está casi detenido, no hay nada que evaluar.
        if (initialSpeed < 10) {
            Log.d("TripViewModel", "El vehículo ya iba lento. No se inicia evaluación.")
            return
        }

        // Lanzamos una corrutina para monitorizar la velocidad.
        redLightEvaluationJob = viewModelScope.launch {
            postAlert("Semáforo en rojo detectado. Reduce la velocidad.", isCritical = true)

            val evaluationTimeSeconds = 10 // Tiene 10 segundos para detenerse.
            var hasStoppedInTime = false

            // Bucle que se ejecuta cada segundo durante 10 segundos.
            repeat(evaluationTimeSeconds) {
                delay(1000) // Espera 1 segundo
                val currentSpeed = _uiState.value.speed

                // Si la velocidad baja a un umbral seguro, el conductor ha reaccionado bien.
                if (currentSpeed < 5) {
                    Log.d("TripViewModel", "¡Frenada Exitosa! Se detuvo a tiempo.")
                    postAlert("¡Bien hecho! Te detuviste correctamente.", isCritical = false)

                    // Pedimos los puntos al experto
                    val points = scoreCalculator.getPointsForRedLightStop(didStopInTime = true)
                    updateScore(_uiState.value.currentScore + points) // Sumamos los puntos

                    hasStoppedInTime = true
                    return@launch // Termina la evaluación.
                }
            }

            // Si el bucle termina y el coche no se ha detenido...
            if (!hasStoppedInTime) {
                Log.w("TripViewModel", "¡Penalización! No se detuvo a tiempo en el semáforo.")
                postAlert("¡Atención! No te detuviste en el semáforo en rojo.", isCritical = true)

                // Pedimos los puntos (negativos) al experto
                val points = scoreCalculator.getPointsForRedLightStop(didStopInTime = false)
                updateScore(_uiState.value.currentScore + points) // Sumamos la penalización
            }
        }
    }
    // ===== FIN: CÓDIGO AÑADIDO =====
}
