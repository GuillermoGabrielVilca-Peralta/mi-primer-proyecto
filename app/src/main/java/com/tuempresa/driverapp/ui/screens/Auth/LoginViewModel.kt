// ruta: C:/Users/Nancy/AndroidStudioProjects/DriverApp/app/src/main/java/com/tuempresa/driverapp/ui/screens/Auth/LoginViewModel.kt
package com.tuempresa.driverapp.ui.screens.Auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.auth.AuthRepository
import com.tuempresa.driverapp.data.managers.SettingsManager // 👇 1. AÑADE ESTE IMPORT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Representa el estado de la pantalla de Login (sin cambios)
data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository, // Hilt nos da el repositorio de Firebase
    private val settingsManager: SettingsManager  // 👇 2. PIDE EL SETTINGSMANAGER AQUÍ
) : ViewModel() {

    // El estado de la UI que la pantalla observará (sin cambios)
    var uiState by mutableStateOf(LoginUiState())
        private set

    // Los campos del formulario (sin cambios)
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // 👇 3. ¡AQUÍ ESTÁ LA PROPIEDAD QUE RESUELVE EL ERROR!
    // Hacemos que el ViewModel exponga la preferencia guardada por el SettingsManager.
    // La LoginScreen leerá esto para saber si debe mostrar el botón de la huella.
    val isBiometricsEnabled = settingsManager.isBiometricAuthEnabled

    fun onEmailChange(newValue: String) {
        email = newValue
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
    }

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Correo y contraseña no pueden estar vacíos.")
            return
        }

        viewModelScope.launch {
            // 1. Mostrar el indicador de carga
            uiState = uiState.copy(isLoading = true, error = null)

            // 2. Llamar al repositorio para iniciar sesión
            val result = authRepository.login(email, password)

            // 3. Procesar el resultado
            if (result.isSuccess) {
                uiState = uiState.copy(isLoading = false, loginSuccess = true)
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                uiState = uiState.copy(isLoading = false, error = "Error al iniciar sesión: $errorMessage")
            }
        }
    }

    // Función para limpiar el mensaje de error cuando el usuario lo descarta
    fun onErrorDismissed() {
        uiState = uiState.copy(error = null)
    }
}
