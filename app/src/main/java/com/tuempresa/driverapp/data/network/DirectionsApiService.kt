package com.tuempresa.driverapp.data.network

import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.GET

interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>
}
