package com.tuempresa.driverapp.data.local.models

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val driverId: String?,
    val message: String?
)
