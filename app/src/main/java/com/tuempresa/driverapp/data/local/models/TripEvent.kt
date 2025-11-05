package com.tuempresa.driverapp.data.local.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_events")
data class TripEvent(
    @PrimaryKey val id: String,
    val tripId: String,
    val type: String,
    val severity: Int,
    val timestamp: Long,
    val metadata: String
)
