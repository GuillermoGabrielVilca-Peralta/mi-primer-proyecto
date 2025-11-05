package com.tuempresa.driverapp.data.local.models

data class DriverScoreResponse(
    val driverId: String,
    val averageScore: Int,
    val totalTrips: Int
)