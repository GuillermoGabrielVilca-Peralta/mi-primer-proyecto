package com.tuempresa.driverapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class Driver(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val faceEmbedding: ByteArray? = null
)
