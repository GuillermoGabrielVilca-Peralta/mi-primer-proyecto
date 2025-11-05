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

    @Query("SELECT * FROM trips WHERE synced = 0")
    suspend fun getUnsyncedTrips(): List<Trip>

    @Query("UPDATE trips SET synced = 1 WHERE id = :tripId")
    suspend fun markSynced(tripId: Int)

    @Query("SELECT * FROM trips ORDER BY startTs DESC")
    fun getAllTrips(): Flow<List<Trip>>
}
