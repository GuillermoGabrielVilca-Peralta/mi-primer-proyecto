// ruta: app/src/main/java/com/tuempresa/driverapp/ui/screens/ActiveTripViewModel.kt
package com.tuempresa.driverapp.ui.screens

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.remote.DetectionApiService
import com.tuempresa.driverapp.data.repository.DriverRepository
import com.tuempresa.driverapp.domain.ScoreCalculator
// 👇 1. IMPORTA LAS CLASES NECESARIAS
import com.tuempresa.driverapp.data.local.models.TripEvent
import java.util.UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.text.toIntOrNull

data class ActiveTripUiState(
    val speed: Int = 0,
    val alertMessage: String = "Iniciando viaje...",
    val isCriticalAlert: Boolean = false,
    val currentScore: Int = 100
)

@HiltViewModel
class ActiveTripViewModel @Inject constructor(
    private val detectionApi: DetectionApiService,
    private val repo: DriverRepository,
    private val scoreCalculator: ScoreCalculator,

    application: Application
) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow(ActiveTripUiState())
    val uiState: StateFlow<ActiveTripUiState> = _uiState.asStateFlow()

    private var monitoringJob: Job? = null
    private lateinit var tts: TextToSpeech

    // 👇 2. AÑADE UNA VARIABLE PARA GUARDAR EL ID DEL VIAJE ACTUAL
    private var currentTripId: String? = null

    // ... (dentro de la clase ActiveTripViewModel)
    // 👇 AÑADE ESTAS DOS LÍNEAS AQUÍ 👇
    private var tripStartTime: Long = 0L
    private var maxSpeedDuringTrip: Int = 0


    init {
        tts = TextToSpeech(application.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("es", "ES")
        }
    }

    private fun speak(text: String) {
        if (::tts.isInitialized) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    // 👇 3. NUEVA FUNCIÓN PARA CENTRALIZAR PENALIZACIONES
    private fun recordAndApplyPenalty(
        points: Int,
        eventType: String,
        severity: Int,
        metadata: String,
        alertMessage: String,
        ttsMessage: String
    ) {
        // Solo aplica la penalización si el mensaje es nuevo, para evitar duplicados
        if (_uiState.value.alertMessage == alertMessage) return

        // Actualiza el puntaje en la UI y en la base de datos
        val newScore = (_uiState.value.currentScore + points).coerceIn(0, 100)
        _uiState.update { it.copy(currentScore = newScore, alertMessage = alertMessage, isCriticalAlert = true) }
        viewModelScope.launch { repo.updateLiveScore(newScore) }

        // Habla si hay un mensaje que decir
        speak(ttsMessage)

        // Guarda el evento detallado en la base de datos
        viewModelScope.launch {
            currentTripId?.let { tripId ->
                val event = TripEvent(
                    id = UUID.randomUUID().toString(),
                    tripId = tripId,
                    type = eventType,
                    severity = severity,
                    timestamp = System.currentTimeMillis(),
                    metadata = metadata
                )
                repo.saveTripEvent(event)
                Log.d("ActiveTripVM", "Evento guardado: $eventType")
            }
        }
    }

    fun startTripMonitoring() {
        if (monitoringJob?.isActive == true) return

        // 👇 4. ASIGNA UN ID ÚNICO AL INICIAR EL VIAJE
        currentTripId = UUID.randomUUID().toString()
        tripStartTime = System.currentTimeMillis()
        Log.d("ActiveTripVM", "Nuevo viaje iniciado con ID: $currentTripId")

        // Carga el puntaje inicial guardado al comenzar el viaje
        viewModelScope.launch {
            // Usamos .first() para obtener el primer valor y no seguir escuchando
            val savedScore = repo.getLiveScore().firstOrNull() ?: 100
            _uiState.update { it.copy(currentScore = savedScore, alertMessage = "Viaje iniciado. ¡Conduce con cuidado!") }
        }

        monitoringJob = viewModelScope.launch {
            // Simulación de velocidad
            // Dentro de startTripMonitoring(), en el launch de la simulación de velocidad

            launch {
                var currentSpeed = 50
                while (true) {
                    currentSpeed += (-5..5).random()
                    val finalSpeed = currentSpeed.coerceIn(0, 120)

                    // 👇 AÑADE ESTA LÍNEA AQUÍ 👇
                    if (finalSpeed > maxSpeedDuringTrip) maxSpeedDuringTrip = finalSpeed

                    _uiState.update { it.copy(speed = finalSpeed) }
                    delay(2000)
                }
            }

            // Monitoreo de eventos
            try {
                while (true) {
                    val detection = detectionApi.getLatestState()
                    val currentSpeed = _uiState.value.speed

                    when (detection.evento) {
                        "semaforo_rojo" -> {
                            // 👇 5. USA LA NUEVA FUNCIÓN PARA GESTIONAR LA PENALIZACIÓN
                            recordAndApplyPenalty(
                                points = scoreCalculator.getPointsForRedLightStop(didStopInTime = false),
                                eventType = "SEMAFORO_ROJO",
                                severity = 5, // La más alta
                                metadata = "Detección de servidor",
                                alertMessage = "¡¡ALERTA!! SEMÁFORO EN ROJO DETECTADO",
                                ttsMessage = "Alerta, Semáforo en rojo"
                            )
                        }

                        "limite_velocidad" -> {
                            val speedLimit = detection.valor.toString().toIntOrNull() ?: 0
                            if (currentSpeed > speedLimit && speedLimit > 0) {
                                // 👇 6. USA LA NUEVA FUNCIÓN TAMBIÉN AQUÍ
                                recordAndApplyPenalty(
                                    points = scoreCalculator.getPointsForSpeeding(currentSpeed, speedLimit),
                                    eventType = "EXCESO_VELOCIDAD",
                                    severity = 3, // Media
                                    metadata = "Límite: $speedLimit km/h, Velocidad: $currentSpeed km/h",
                                    alertMessage = "¡¡ALERTA!! EXCESO DE VELOCIDAD. LÍMITE: $speedLimit km/h",
                                    ttsMessage = "¡Vas a $currentSpeed! Reduce la velocidad. El límite es $speedLimit."
                                )
                            } else {
                                // Lógica para cuando se conduce correctamente (sin penalización)
                                val alert = "Límite de velocidad: $speedLimit km/h"
                                if (_uiState.value.alertMessage != alert) {
                                    _uiState.update { it.copy(alertMessage = alert, isCriticalAlert = false) }
                                }
                            }
                        }

                        "ninguno" -> {
                            val alert = "Todo OK. ¡Buen trabajo!"
                            if (_uiState.value.alertMessage != alert) {
                                _uiState.update { it.copy(alertMessage = alert, isCriticalAlert = false) }
                            }
                        }
                        else -> {
                            val alert = "Evento desconocido: ${detection.evento}"
                            if (_uiState.value.alertMessage != alert) {
                                _uiState.update { it.copy(alertMessage = alert, isCriticalAlert = false) }
                            }
                        }
                    }

                    delay(1500)
                }
            } catch (e: Exception) {
                val errorMessage = "Error de conexión con el servidor."
                _uiState.update { it.copy(alertMessage = errorMessage, isCriticalAlert = true) }
                speak(errorMessage)
                Log.e("ActiveTripVM", "Error en monitoreo", e)
            }
        }
    }
    // En ActiveTripViewModel.kt, después de startTripMonitoring()

    // 👇 COPIA Y PEGA TODA ESTA FUNCIÓN 👇
    fun finishTrip() {
        // 1. Detiene el monitoreo para no seguir registrando eventos.
        monitoringJob?.cancel()

        // 2. Lanza una corrutina para hacer el trabajo de guardado en segundo plano.
        viewModelScope.launch {
            val tripId = currentTripId ?: return@launch // No guardar si no hay viaje
            val finalScore = _uiState.value.currentScore
            val endTime = System.currentTimeMillis()
            val durationInSeconds = ((endTime - tripStartTime) / 1000).toInt()

            // 3. Crea el objeto 'Trip' con todos los datos recopilados.
            val tripToSave = com.tuempresa.driverapp.data.local.models.Trip(
                id = tripId,
                driverId = "driver_01", // Temporal: Esto debería venir del usuario logueado
                startTs = tripStartTime,
                endTs = endTime,
                timeActiveSeconds = durationInSeconds,
                finalScore = finalScore,
                maxSpeed = maxSpeedDuringTrip.toDouble(),
                // Los siguientes son valores simulados. En un futuro se podrían calcular.
                avgSpeed = if (durationInSeconds > 0) 60.0 else 0.0, // Simulación
                distanceKm = if (durationInSeconds > 0) (durationInSeconds / 3600.0) * 60.0 else 0.0, // Simulación
                isSynced = false
            )

            // 4. Llama al repositorio para que guarde el viaje en la base de datos.
            try {
                repo.saveTrip(tripToSave)
                Log.d("ActiveTripVM", "Viaje guardado con ID: $tripId y puntaje: $finalScore")
            } catch (e: Exception) {
                Log.e("ActiveTripVM", "Error al guardar el viaje.", e)
                // Opcional: Podrías mostrar un error al usuario.
            }

            // 5. Resetea las variables para el próximo viaje.
            resetTripState()
        }
    }

    // 👇 AÑADE TAMBIÉN ESTA FUNCIÓN AUXILIAR 👇
    private fun resetTripState() {
        currentTripId = null
        tripStartTime = 0L
        maxSpeedDuringTrip = 0
        // Opcional: resetear la UI a su estado inicial
        _uiState.value = ActiveTripUiState()
    }


    override fun onCleared() {
        monitoringJob?.cancel()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onCleared()
    }
}
