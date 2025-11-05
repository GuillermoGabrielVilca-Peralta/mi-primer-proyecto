package com.tuempresa.driverapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tuempresa.driverapp.data.local.models.Driver

@Dao
interface DriverDao {

    @Insert
    suspend fun insert(driver: Driver)

    @Query("SELECT * FROM drivers WHERE id = :id")
    suspend fun getDriverById(id: String): Driver?

    @Query("DELETE FROM drivers")
    suspend fun deleteAll()
}
