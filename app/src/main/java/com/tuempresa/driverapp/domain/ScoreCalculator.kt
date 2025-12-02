// ruta: app/src/main/java/com/tuempresa/driverapp/domain/ScoreCalculator.kt
package com.tuempresa.driverapp.domain

import com.tuempresa.driverapp.data.local.models.TripEvent
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Añade Singleton para que Hilt use una sola instancia
class ScoreCalculator @Inject constructor() { // Hilt puede crear instancias de esta clase

    fun calculateFinalScore(events: List<TripEvent>, excesoPromKmh: Double): Int {
        var score = 100 // Puntuación base

        events.forEach { e ->
            when(e.type) {
                "frenada" -> score -= 10 * e.severity
                "acel_br" -> score -= 5 * e.severity
                "uso_cel" -> {
                    val sec = JSONObject(e.metadata).optInt("duration",0)
                    score -= 20 * (sec / 10) // Penalización por cada 10s de uso
                }
                "somnolencia" -> score -= 30 * e.severity
                "salida_carril" -> score -= 15 * e.severity
                // Tu ViewModel ya penaliza el semáforo en rojo en tiempo real,
                // así que podríamos quitar "rojo" de aquí para no penalizar dos veces.
                // Lo dejo por si lo usas en otro sitio.
                "rojo" -> score -= 25
            }
        }

        // Penalización por exceso de velocidad promedio
        score -= (excesoPromKmh * 0.5).toInt()

        // Recompensa si no hubo incidentes
        if (events.isEmpty()) {
            score += 20
        }

        return score.coerceIn(0, 100) // Asegura que la puntuación esté entre 0 y 100
    }

    fun getPointsForRedLightStop(didStopInTime: Boolean): Int {
        return if (didStopInTime) {
            +5 // Recompensa por detenerse
        } else {
            -25 // Penalización fuerte por no detenerse
        }
    }

    // 👇 AÑADE ESTA FUNCIÓN QUE FALTABA
    /**
     * Calcula los puntos de penalización en tiempo real por exceso de velocidad.
     * La penalización es mayor cuanto más se excede el límite.
     */
    fun getPointsForSpeeding(currentSpeed: Int, speedLimit: Int): Int {
        // No penalizar si el límite de velocidad no es válido (es 0 o negativo)
        if (speedLimit <= 0) {
            return 0
        }

        val excess = currentSpeed - speedLimit
        return when {
            excess > 20 -> -15 // Penalización severa por más de 20 km/h de exceso
            excess > 10 -> -10 // Penalización moderada por más de 10 km/h
            excess > 0  -> -5  // Penalización leve por cualquier exceso
            else        -> 0  // Sin penalización si no se excede el límite
        }
    }
}
