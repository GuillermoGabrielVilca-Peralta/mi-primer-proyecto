// ruta: app/src/main/java/com/tuempresa/driverapp/data/local/db/dao/TripDao.ktpackage com.tuempresa.driverapp.data.local.db.dao
package com.tuempresa.driverapp.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tuempresa.driverapp.data.local.models.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    // 👇 CORRECCIÓN PRINCIPAL: Cambiar 'synced' a 'isSynced' 👇
    @Query("SELECT * FROM trips WHERE isSynced = 0")
    suspend fun getUnsyncedTrips(): List<Trip>

    // 👇 CORRECCIÓN AQUÍ TAMBIÉN: Cambiar 'synced' a 'isSynced' 👇
    @Query("UPDATE trips SET isSynced = 1 WHERE id = :tripId")
    suspend fun markTripAsSynced(tripId: String) // Renombrado para más claridad

    @Query("SELECT * FROM trips ORDER BY startTs DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripById(tripId: String): Flow<Trip?>
}
