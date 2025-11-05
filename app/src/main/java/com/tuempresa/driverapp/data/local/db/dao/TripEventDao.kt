package com.tuempresa.driverapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tuempresa.driverapp.data.local.models.TripEvent

@Dao
interface TripEventDao {

    @Insert
    suspend fun insert(event: TripEvent)

    @Query("SELECT * FROM trip_events WHERE tripId = :tripId")
    suspend fun getEventsByTrip(tripId: String): List<TripEvent>

    @Query("DELETE FROM trip_events")
    suspend fun deleteAll()
}
