package com.tuempresa.driverapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tuempresa.driverapp.data.local.dao.DriverDao
import com.tuempresa.driverapp.data.local.db.dao.TripDao
import com.tuempresa.driverapp.data.local.dao.TripEventDao
import com.tuempresa.driverapp.data.local.models.Driver
import com.tuempresa.driverapp.data.local.models.Trip
import com.tuempresa.driverapp.data.local.models.TripEvent


@Database(entities = [Driver::class, Trip::class, TripEvent::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun driverDao(): DriverDao
    abstract fun tripEventDao(): TripEventDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "driver_db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
    
}
