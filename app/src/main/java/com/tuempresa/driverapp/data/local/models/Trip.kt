package com.tuempresa.driverapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey val id: String,
    val driverId: String,
    val routeJson: String,
    val startTs: Long,
    val endTs: Long?,
    val timeActiveSeconds: Int,
    val baseScore: Int,
    val finalScore: Int,
    val synced: Boolean = false
)
