// ruta: app/src/main/java/com/tuempresa/driverapp/data/auth/AuthRepository.kt
package com.tuempresa.driverapp.data.auth

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getUser(): Flow<FirebaseUser?>
    suspend fun login(email: String, password: String): Result<Unit>
    // ...
    // ...
    suspend fun signUp(email: String, password: String, name: String): Result<Unit>
    // ...
    // ...
    fun logout()
    suspend fun updateProfile(name: String): Result<Unit>
}
