// ruta: C:/Users/Nancy/AndroidStudioProjects/DriverApp/app/src/main/java/com/tuempresa/driverapp/worker/SyncTripsWorker.kt
package com.tuempresa.driverapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
            val unsynced = db.tripDao().getUnsyncedTrips()

            if (unsynced.isEmpty()) {
                return@withContext Result.success()
            }

            val api = ApiClient.service

            for (trip in unsynced) {
                val tripRemote = TripRemote.fromLocal(trip)

                val response = api.uploadTrip(tripRemote)
                if (response.isSuccessful) {
                    // 👇 ¡AQUÍ ESTÁ LA CORRECCIÓN!
                    // 'trip.id' ya es un String, se pasa directamente.
                    db.tripDao().markTripAsSynced(trip.id)
                } else {
                    // Si falla la subida de un viaje, reintentamos más tarde.
                    return@withContext Result.retry()
                }
            }

            // Si todos los viajes se subieron con éxito.
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Si ocurre cualquier otro error (ej: sin red), reintentamos.
            Result.retry()
        }
    }
}
