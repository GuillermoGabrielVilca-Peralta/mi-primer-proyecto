package com.tuempresa.driverapp.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.tuempresa.driverapp.data.local.db.AppDatabase
import com.tuempresa.driverapp.data.remote.ApiClient
import com.tuempresa.driverapp.data.remote.ApiService
import com.tuempresa.driverapp.data.remote.DetectionApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient // <-- 1. IMPORTA ESTO
import okhttp3.logging.HttpLoggingInterceptor // <-- 2. IMPORTA ESTO
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiClient.service
    }

    @Provides
    @Singleton
    fun provideDetectionApiService(): DetectionApiService {
        val moshi = Moshi.Builder().build()

        // --- INICIO DE LA MODIFICACIÓN CRUCIAL ---

        // 3. Crea un interceptor que registrará cada detalle de la petición de red.
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // .BODY muestra todo: URL, método, headers, body, respuesta, errores.
        }

        // 4. Crea un cliente de red (OkHttpClient) y añádele el interceptor.
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        // --- FIN DE LA MODIFICACIÓN ---

        return Retrofit.Builder()
            // 5. Asigna el cliente con el "espía" a Retrofit.
            .client(okHttpClient)
            // La IP para el emulador es correcta.
            .baseUrl("http://172.22.147.247:5000/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(DetectionApiService::class.java)
    }
}
