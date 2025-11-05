package com.tuempresa.driverapp.data.remote

import com.tuempresa.driverapp.data.local.models.LoginRequest
import com.tuempresa.driverapp.data.local.models.LoginResponse
import com.tuempresa.driverapp.data.local.models.TripRemote
import com.tuempresa.driverapp.data.local.models.TripResponse
import com.tuempresa.driverapp.data.local.models.DriverScoreResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("api/trips")
    suspend fun uploadTrip(@Body trip: TripRemote): Response<TripResponse>

    @GET("api/drivers/{id}/score")
    suspend fun getDriverScore(@Path("id") driverId: String): Response<DriverScoreResponse>
}
