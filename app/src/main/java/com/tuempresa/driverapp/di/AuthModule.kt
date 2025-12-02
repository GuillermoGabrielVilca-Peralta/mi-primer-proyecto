// ruta: app/src/main/java/com/tuempresa/driverapp/di/AuthModule.kt
package com.tuempresa.driverapp.di

import com.tuempresa.driverapp.data.auth.AuthRepository
import com.tuempresa.driverapp.data.auth.FirebaseAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    /**
     * Esta función le dice a Hilt: "Cuando alguien pida un 'AuthRepository',
     * dale una instancia de 'FirebaseAuthRepository'".
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository
    ): AuthRepository
}
