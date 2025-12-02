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
import com.tuempresa.driverapp.data.local.models.DriverScore
import com.tuempresa.driverapp.data.local.db.dao.ScoreDao

// 👇 CAMBIO 1: INCREMENTA LA VERSIÓN UNA VEZ MÁS 👇
@Database(entities = [Driver::class, Trip::class, TripEvent::class, DriverScore::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun driverDao(): DriverDao
    abstract fun tripEventDao(): TripEventDao
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    // 👇 CAMBIO 2: AJUSTA LIGERAMENTE EL NOMBRE DEL ARCHIVO DE LA BD 👇
                    "driver_db_v4"
                )
                    // Esta línea es correcta y crucial. Borra la BD antigua y crea una nueva.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
