// ruta: C:/.../driverapp/data/local/models/TripRemote.kt
package com.tuempresa.driverapp.data.local.models

import com.google.gson.Gson // Importación en su propia línea

// 👇 CAMBIO: Todos los 'val' van DENTRO de los paréntesis 👇
data class TripRemote(
    val id: String,
    val driverId: String,
    val durationSeconds: Int,
    val score: Int,
    val maxSpeed: Double,
    val avgSpeed: Double,
    val distanceKm: Double,
    // Opcional: si quieres enviar la ruta, podrías construirla aquí
    val routeJson: String? = null
) {
    companion object {
        fun fromLocal(local: Trip): TripRemote {
            // Ya no hay 'routeJson' en el modelo local, así que lo eliminamos.
            // Si necesitaras enviar una ruta, tendrías que obtenerla de otra fuente
            // (por ejemplo, de los TripEvents asociados a este viaje).
            // Por ahora, lo dejamos como nulo o lo eliminamos de TripRemote.

            // La duración ya la calculamos y guardamos en 'timeActiveSeconds'.
            val duration = local.timeActiveSeconds

            return TripRemote(
                id = local.id,
                driverId = local.driverId,
                durationSeconds = duration,
                score = local.finalScore,
                maxSpeed = local.maxSpeed,
                avgSpeed = local.avgSpeed,
                distanceKm = local.distanceKm
                // routeJson = "[]" // Opcional: enviar un JSON vacío si el servidor lo requiere
            )
        }
    }
}
