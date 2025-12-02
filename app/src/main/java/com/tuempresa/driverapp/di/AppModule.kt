// ruta: C:/Users/Nancy/AndroidStudioProjects/DriverApp/app/src/main/java/com/tuempresa/driverapp/di/AppModule.kt.
package com.tuempresa.driverapp.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.tuempresa.driverapp.data.local.db.AppDatabase
import com.tuempresa.driverapp.data.local.db.dao.ScoreDao
import com.tuempresa.driverapp.data.local.db.dao.TripDao
// 👇 1. IMPORTA EL DAO QUE FALTA
import com.tuempresa.driverapp.data.local.dao.TripEventDao
import com.tuempresa.driverapp.data.remote.ApiClient
import com.tuempresa.driverapp.data.remote.ApiService
import com.tuempresa.driverapp.data.remote.DetectionApiService
import com.tuempresa.driverapp.data.repository.DriverRepository
import com.tuempresa.driverapp.data.repository.DriverRepositoryImpl
// 👇 EXTRA: AÑADE TAMBIÉN EL CALCULADOR DE PUNTAJE, LO NECESITAREMOS PRONTO
import com.tuempresa.driverapp.domain.ScoreCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- PROVEEDORES DE SERVICIOS REMOTOS (Sin cambios) ---
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiClient.service
    }

    @Provides
    @Singleton
    fun provideDetectionApiService(): DetectionApiService {
        val moshi = Moshi.Builder().build()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://10.29.235.216:5000/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(DetectionApiService::class.java)
    }

    // --- PROVEEDORES DE BASE DE DATOS Y DAOs ---

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideScoreDao(database: AppDatabase): ScoreDao {
        return database.scoreDao()
    }

    @Provides
    @Singleton
    fun provideTripDao(database: AppDatabase): TripDao {
        return database.tripDao()
    }

    // 👇 2. AÑADE LA FUNCIÓN PARA PROVEER TripEventDao
    @Provides
    @Singleton
    fun provideTripEventDao(database: AppDatabase): TripEventDao {
        return database.tripEventDao()
    }

    // --- PROVEEDOR DEL REPOSITORIO Y DOMINIO ---

    // 👇 3. ACTUALIZA LA FUNCIÓN QUE PROVEE EL REPOSITORIO
    @Provides
    @Singleton
    fun provideDriverRepository(
        tripDao: TripDao,
        scoreDao: ScoreDao,
        tripEventDao: TripEventDao // <-- Pide la nueva dependencia
    ): DriverRepository {
        // Pasa las TRES dependencias al constructor
        return DriverRepositoryImpl(
            tripDao = tripDao,
            scoreDao = scoreDao,
            tripEventDao = tripEventDao // <-- Pásala aquí
        )
    }

    // Añade esto para que los ViewModels puedan usar la lógica de puntajes
    @Provides
    @Singleton
    fun provideScoreCalculator(): ScoreCalculator {
        return ScoreCalculator()
    }
}
