package com.tuempresa.driverapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driver_score")
data class DriverScore(
    @PrimaryKey val id: Int = 1,
    val score: Int,
    val totalTrips: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
