package com.tuempresa.driverapp.data.repository


import com.tuempresa.driverapp.data.local.db.AppDatabase
import com.tuempresa.driverapp.data.local.models.Trip
import com.tuempresa.driverapp.data.remote.ApiService
import com.tuempresa.driverapp.data.local.models.TripRemote
import com.tuempresa.driverapp.data.local.models.TripResponse
import javax.inject.Inject

class DriverRepository @Inject constructor(
    private val db: AppDatabase,
    private val api: ApiService
) {
    suspend fun saveTripLocal(trip: Trip) = db.tripDao().insertTrip(trip)

    suspend fun uploadTripRemote(tripRemote: TripRemote): Result<TripResponse> {
        return try {
            val resp = api.uploadTrip(tripRemote)
            if (resp.isSuccessful) Result.success(resp.body()!!)
            else Result.failure(Exception("Error ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun getAllTrips() = db.tripDao().getAllTrips()

    suspend fun getUnsyncedTrips() = db.tripDao().getUnsyncedTrips()
}
