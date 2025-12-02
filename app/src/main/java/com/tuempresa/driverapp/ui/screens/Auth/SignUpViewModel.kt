// ruta: app/src/main/java/com/tuempresa/driverapp/ui/screens/Auth/SignUpViewModel.kt
package com.tuempresa.driverapp.ui.screens.Auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Usaremos el mismo UiState que en el Login
data class SignUpUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val signUpSuccess: Boolean = false
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(SignUpUiState())
        private set

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    // 👇 1. AÑADIMOS UNA VARIABLE PARA GUARDAR EL NOMBRE
    var name by mutableStateOf("")

    fun onEmailChange(newValue: String) { email = newValue }
    fun onPasswordChange(newValue: String) { password = newValue }
    fun onConfirmPasswordChange(newValue: String) { confirmPassword = newValue }
    // 👇 2. AÑADIMOS UNA FUNCIÓN PARA ACTUALIZAR EL NOMBRE
    fun onNameChange(newValue: String) { name = newValue }

    fun signUp() {
        if (password != confirmPassword) {
            uiState = uiState.copy(error = "Las contraseñas no coinciden.")
            return
        }
        // 👇 3. AÑADIMOS LA VALIDACIÓN PARA EL NOMBRE
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            uiState = uiState.copy(error = "Ningún campo puede estar vacío.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            // 👇 4. PASAMOS EL NOMBRE A LA FUNCIÓN DEL REPOSITORIO
            val result = authRepository.signUp(email, password, name)
            if (result.isSuccess) {
                uiState = uiState.copy(isLoading = false, signUpSuccess = true)
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                uiState = uiState.copy(isLoading = false, error = "Error al registrar: $errorMessage")
            }
        }
    }

    fun onErrorDismissed() {
        uiState = uiState.copy(error = null)
    }
}
