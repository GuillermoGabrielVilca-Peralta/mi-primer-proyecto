// ruta: app/src/main/java/com/tuempresa/driverapp/data/managers/BiometricAuthManager.kt
package com.tuempresa.driverapp.data.managers

import android.content.Context
// 👇 1. CORREGIMOS LA IMPORTACIÓN DE AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.authenticate
// 👇 2. ELIMINAMOS LA IMPORTACIÓN INCORRECTA
// import androidx.biometric.auth.authenticate
import androidx.core.content.ContextCompat
//import dagger.hilt.android.qualifiers.ActivityContext
//import javax.inject.Inject

// Enum para los resultados posibles
enum class BiometricAuthStatus {
    SUCCESS, // Éxito
    ERROR,   // Error durante la autenticación
    UNAVAILABLE, // El dispositivo no tiene hardware o no está configurado
    CANCELLED // El usuario canceló la operación
}

class BiometricAuthManager(private val context: Context) {
    // ... el resto del archivo es exactamente igual ...
    fun checkBiometricSupport(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    // Muestra el diálogo de autenticación al usuario.
    fun showBiometricPrompt(
        title: String,
        subtitle: String,
        onResult: (BiometricAuthStatus) -> Unit // Callback para devolver el resultado
    ) {
        // No se puede continuar si el contexto no es una Actividad (necesario para el diálogo)
        val activity = context as? AppCompatActivity ?: return

        val executor = ContextCompat.getMainExecutor(context)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Cancelar") // Texto del botón de cancelación
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                // Se llama cuando hay un error irrecuperable.
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Si el error es porque el usuario canceló, lo manejamos específicamente
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED || errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        onResult(BiometricAuthStatus.CANCELLED)
                    } else {
                        onResult(BiometricAuthStatus.ERROR)
                    }
                }

                // Se llama cuando la autenticación es exitosa.
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(BiometricAuthStatus.SUCCESS)
                }

                // Se llama cuando la huella no es reconocida.
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // El diálogo se queda abierto, no es necesario devolver un error aquí.
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }
}
