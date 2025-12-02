// ruta: app/src/main/java/com/tuempresa/driverapp/data/repository/DriverRepositoryImpl.kt
package com.tuempresa.driverapp.data.repository

import com.tuempresa.driverapp.data.local.db.dao.ScoreDao
import com.tuempresa.driverapp.data.local.db.dao.TripDao
import com.tuempresa.driverapp.data.local.dao.TripEventDao
import com.tuempresa.driverapp.data.local.models.TripEvent
import com.tuempresa.driverapp.data.local.models.DriverScore
import com.tuempresa.driverapp.data.local.models.Trip
import kotlinx.coroutines.flow.Flow
// 👇 1. IMPORTA EL OPERADOR 'map' PARA TRANSFORMAR EL FLOW
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val scoreDao: ScoreDao,
    private val tripEventDao: TripEventDao
) : DriverRepository {

    override fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips()
    }

    override suspend fun saveTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    override fun getTripById(tripId: String): Flow<Trip?> {
        return tripDao.getTripById(tripId)
    }

    override suspend fun getUnsyncedTrips(): List<Trip> {
        return tripDao.getUnsyncedTrips()
    }

    // CORRECTO
    override suspend fun markTripAsSynced(tripId: String) {
        // 👇 Cambia 'markSynced' por 'markTripAsSynced' 👇
        tripDao.markTripAsSynced(tripId)
    }

    override suspend fun updateLiveScore(newScoreValue: Int) {
        val currentScoreEntity = scoreDao.getCurrentScore()
        val totalTrips = currentScoreEntity?.totalTrips ?: 0

        val newScoreEntity = DriverScore(
            id = 1,
            score = newScoreValue,
            totalTrips = totalTrips
        )
        scoreDao.upsert(newScoreEntity)
    }

    override suspend fun saveTripEvent(event: TripEvent) {
        tripEventDao.insertEvent(event)
    }

    override fun getEventsForTrip(tripId: String): Flow<List<TripEvent>> {
        return tripEventDao.getEventsForTrip(tripId)
    }

    // 👇 2. AÑADE LA IMPLEMENTACIÓN DE LA FUNCIÓN QUE FALTABA
    /**
     * Obtiene el puntaje como un Flow<Int?>.
     * Llama al DAO y transforma el resultado para devolver solo el número.
     */
    override fun getLiveScore(): Flow<Int?> {
        return scoreDao.getScoreFlow().map { driverScore ->
            driverScore?.score
        }
    }
}
