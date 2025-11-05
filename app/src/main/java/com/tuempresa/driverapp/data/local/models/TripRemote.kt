package com.tuempresa.driverapp.data.local.models

data class TripRemote(
    val id: String,
    val driverId: String,
    val routeJson: String,
    val duration: Long,
    val score: Int
) {
    companion object {
        fun fromLocal(local: Trip): TripRemote {
            return TripRemote(
                id = local.id,
                driverId = local.driverId,
                routeJson = local.routeJson,
                duration = (local.endTs ?: local.startTs) - local.startTs,
                score = local.finalScore
            )
        }
    }
}
