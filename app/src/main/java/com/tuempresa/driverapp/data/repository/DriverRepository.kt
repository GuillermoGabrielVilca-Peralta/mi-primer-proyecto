// ruta: app/src/main/java/com/tuempresa/driverapp/data/repository/DriverRepository.kt
package com.tuempresa.driverapp.data.repository

import com.tuempresa.driverapp.data.local.models.Trip
// 👇 1. IMPORTA LAS CLASES NECESARIAS
import com.tuempresa.driverapp.data.local.models.TripEvent
import kotlinx.coroutines.flow.Flow

interface DriverRepository {

    fun getAllTrips(): Flow<List<Trip>>

    suspend fun saveTrip(trip: Trip)

    fun getTripById(tripId: String): Flow<Trip?>

    suspend fun getUnsyncedTrips(): List<Trip>

    suspend fun markTripAsSynced(tripId: String)

    suspend fun updateLiveScore(newScoreValue: Int)

    // 👇 2. AÑADE ESTAS DOS NUEVAS FUNCIONES AL FINAL
    /**
     * Guarda un evento específico (ej. penalización) en la base de datos.
     */
    suspend fun saveTripEvent(event: TripEvent)

    /**
     * Obtiene una lista "viva" (Flow) de todos los eventos para un viaje concreto.
     */
    fun getEventsForTrip(tripId: String): Flow<List<TripEvent>>
    fun getLiveScore(): Flow<Int?>
}
