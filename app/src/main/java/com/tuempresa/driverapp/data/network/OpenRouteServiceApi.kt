package com.tuempresa.driverapp.data.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.Call
import com.tuempresa.driverapp.data.network.ORSResponse

interface OpenRouteServiceApi {

    @GET("v2/directions/driving-car")
    fun getRoute(
        @Header("Authorization") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): Call<ORSResponse>
}
