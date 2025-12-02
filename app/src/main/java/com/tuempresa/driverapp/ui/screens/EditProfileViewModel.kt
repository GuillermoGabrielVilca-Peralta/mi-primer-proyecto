// ruta: app/src/main/java/com/tuempresa/driverapp/ui/screens/EditProfileViewModel.kt
package com.tuempresa.driverapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tuempresa.driverapp.data.auth.AuthRepository
import com.tuempresa.driverapp.data.models.DriverProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val loadError: String? = null,
    val updateError: String? = null,
    val updateSuccess: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(EditProfileUiState())
        private set

    // Estado para el campo de texto del nombre
    var name by mutableStateOf("")
        private set

    fun onNameChange(newName: String) {
        name = newName
    }

    init {
        // Al iniciar el ViewModel, carga los datos actuales del usuario.
        loadCurrentUserProfile()
    }

    private fun loadCurrentUserProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val userId = Firebase.auth.currentUser?.uid
            if (userId == null) {
                uiState = uiState.copy(isLoading = false, loadError = "No se pudo encontrar el usuario.")
                return@launch
            }

            try {
                // Leemos los datos una sola vez desde Realtime Database
                val snapshot = Firebase.database.getReference("drivers").child(userId).get().await()
                val profile = snapshot.getValue(DriverProfile::class.java)

                if (profile != null) {
                    name = profile.name // Actualizamos el campo de texto con el nombre actual
                    uiState = uiState.copy(isLoading = false)
                } else {
                    uiState = uiState.copy(isLoading = false, loadError = "No se encontró el perfil del conductor.")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, loadError = "Error al cargar el perfil: ${e.message}")
            }
        }
    }

    fun onSaveProfile() {
        if (name.isBlank()) {
            uiState = uiState.copy(updateError = "El nombre no puede estar vacío.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, updateError = null)
            val result = authRepository.updateProfile(name)
            if (result.isSuccess) {
                uiState = uiState.copy(isLoading = false, updateSuccess = true)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error desconocido al actualizar."
                uiState = uiState.copy(isLoading = false, updateError = error)
            }
        }
    }

    fun onErrorDismissed() {
        uiState = uiState.copy(loadError = null, updateError = null)
    }
}
