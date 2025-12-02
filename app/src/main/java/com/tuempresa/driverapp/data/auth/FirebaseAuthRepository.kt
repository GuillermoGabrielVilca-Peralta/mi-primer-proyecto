// ruta: app/src/main/java/com/tuempresa/driverapp/data/auth/FirebaseAuthRepository.kt
package com.tuempresa.driverapp.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
// 👇 IMPORTA LA LIBRERÍA DE REALTIME DATABASE
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
// 👇 IMPORTA TU MODELO DE DATOS
import com.tuempresa.driverapp.data.models.DriverProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor() : AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth
    //OBTÉN UNA REFERENCIA A REALTIME DATABASE
    private val database = Firebase.database

    // Esta función está perfecta, no se toca
    override fun getUser(): Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    // El login está perfecto, no se toca
    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 👇 ¡AQUÍ ESTÁ LA MAGIA! ACTUALIZAMOS LA FUNCIÓN SIGNUP
    override suspend fun signUp(email: String, password: String, name: String): Result<Unit> {
        return try {
            // Primero, crea el usuario en Firebase Authentication (como ya hacías)
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: throw IllegalStateException("Firebase user no puede ser nulo después del registro.")

            // Después, prepara el objeto del perfil con los datos del nuevo usuario
            val driverProfile = DriverProfile(uid = user.uid, email = user.email!!, name = name)

            // Finalmente, guarda ese perfil en Realtime Database
            // en un nodo llamado "drivers" y usando el UID como identificador único.
            database.getReference("drivers").child(user.uid).setValue(driverProfile).await()

            // Si todo fue bien, devuelve el éxito
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // El logout está perfecto, no se toca
    override fun logout() {
        auth.signOut()
    }
    override suspend fun updateProfile(name: String): Result<Unit> {
        return try {
            // Obtenemos el UID del usuario actual. Si no está logueado, es un error.
            val userId = auth.currentUser?.uid
                ?: throw IllegalStateException("No hay un usuario autenticado para actualizar el perfil.")

            // En Realtime Database, para actualizar un campo específico (como 'name')
            // sin borrar los demás (como 'email' y 'uid'), usamos updateChildren.
            val profileUpdates = mapOf("name" to name)

            database.getReference("drivers").child(userId)
                .updateChildren(profileUpdates)
                .await() // Esperamos a que la operación termine

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
    