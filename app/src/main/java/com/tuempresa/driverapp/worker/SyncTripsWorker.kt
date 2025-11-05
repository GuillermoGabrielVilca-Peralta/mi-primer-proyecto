package com.tuempresa.driverapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Worker
import com.tuempresa.driverapp.data.local.db.AppDatabase
import com.tuempresa.driverapp.data.remote.ApiClient
import com.tuempresa.driverapp.data.local.models.TripRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncTripsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getInstance(applicationContext)
            val unsynced = db.tripDao().getUnsyncedTrips() // ✅ método que crearemos abajo

            if (unsynced.isEmpty()) return@withContext Result.success()

            val api = ApiClient.service

            for (trip in unsynced) {
                val tripRemote = TripRemote.fromLocal(trip)

                val response = api.uploadTrip(tripRemote)
                if (response.isSuccessful) {
                    db.tripDao().markSynced(trip.id.toInt())
                } else {
                    return@withContext Result.retry()
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
