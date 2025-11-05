package com.tuempresa.driverapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.remote.DetectionApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveTripUiState(
    val speed: Int = 0,
    val alertMessage: String = "Iniciando viaje...",
    val isCriticalAlert: Boolean = false
)

@HiltViewModel
class ActiveTripViewModel @Inject constructor(
    private val detectionApi: DetectionApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveTripUiState())
    val uiState: StateFlow<ActiveTripUiState> = _uiState.asStateFlow()

    private var monitoringJob: Job? = null

    fun startTripMonitoring() {
        // Evita iniciar múltiples bucles si ya hay uno corriendo
        if (monitoringJob?.isActive == true) return

        monitoringJob = viewModelScope.launch {
            // --- BLOQUE 1: SIMULACIÓN DE VELOCIDAD (sin cambios) ---
            launch {
                var currentSpeed = 50
                while (true) {
                    currentSpeed += (-5..5).random()
                    _uiState.update { it.copy(speed = currentSpeed.coerceIn(0, 120)) }
                    delay(2000)
                }
            }

            // --- BLOQUE 2: BUCLE DE MONITOREO DEL SERVIDOR (LÓGICA CORREGIDA) ---
            try {
                while (true) {
                    // 1. OBTENER DATOS: Hacemos la llamada a la API y obtenemos la velocidad actual
                    val detection = detectionApi.getLatestState()
                    val currentSpeed = _uiState.value.speed

                    // 2. PROCESAR DATOS: Decidimos cuál será el nuevo estado de la UI
                    val newAlert: String
                    val newIsCritical: Boolean

                    when (detection.evento) {
                        "semaforo_rojo" -> {
                            newAlert = "¡¡ALERTA!! SEMÁFORO EN ROJO DETECTADO"
                            newIsCritical = true
                        }
                        "limite_velocidad" -> {
                            val speedLimit = detection.valor.toInt()
                            if (currentSpeed > speedLimit) {
                                newAlert = "¡¡ALERTA!! EXCESO DE VELOCIDAD. LÍMITE: $speedLimit km/h"
                                newIsCritical = true
                            } else {
                                newAlert = "Zona de $speedLimit km/h"
                                newIsCritical = false
                            }
                        }
                        "ninguno" -> {
                            newAlert = "Todo OK"
                            newIsCritical = false
                        }
                        else -> {
                            newAlert = "Evento desconocido: ${detection.evento}"
                            newIsCritical = false
                        }
                    }

                    // 3. ACTUALIZAR UI: Aplicamos el nuevo estado una sola vez
                    _uiState.update { it.copy(alertMessage = newAlert, isCriticalAlert = newIsCritical) }

                    // 4. ESPERAR: Pausa antes del siguiente ciclo
                    delay(1500)
                }
            } catch (e: Exception) {
                // Si el bucle falla (p. ej., por pérdida de conexión), se muestra un error y se detiene
                _uiState.update {
                    it.copy(
                        alertMessage = "Error de conexión con el servidor.",
                        isCriticalAlert = true
                    )
                }
                // Imprimimos el error detallado en el Logcat para depuración
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        monitoringJob?.cancel()
        super.onCleared()
    }
}
