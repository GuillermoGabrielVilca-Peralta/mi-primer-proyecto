package com.tuempresa.driverapp.data.remote

import com.squareup.moshi.JsonClass // <-- Importa esto si te lo pide
import retrofit2.http.GET


@JsonClass(generateAdapter = true)
data class DetectionState(
    val evento: String,
    val valor: Int,
    val timestamp: Double
)

interface DetectionApiService {
    @GET("/estado_actual")
    suspend fun getLatestState(): DetectionState
}
