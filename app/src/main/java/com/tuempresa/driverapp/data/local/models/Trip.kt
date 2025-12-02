// ruta: C:/Users/Nancy/.../data/local/models/Trip.kt
package com.tuempresa.driverapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    val id: String,
    val driverId: String,
    val startTs: Long,
    val endTs: Long, // Lo he cambiado a no nulo, ya que un viaje finalizado siempre tiene un endTs.
    val timeActiveSeconds: Int,
    val finalScore: Int,

    // 👇 CAMPOS AÑADIDOS PARA COINCIDIR CON EL VIEWMODEL 👇
    val maxSpeed: Double,
    val avgSpeed: Double,
    val distanceKm: Double,
    val isSynced: Boolean = false
)
