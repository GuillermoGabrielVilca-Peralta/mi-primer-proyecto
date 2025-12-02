// ruta: app/src/main/java/com/tuempresa/driverapp/ui/screens/SettingsViewModel.kt
package com.tuempresa.driverapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.auth.AuthRepository
// 👇 Ya no necesitamos BiometricAuthManager aquí
import com.tuempresa.driverapp.data.managers.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SettingsUiState(
    val logoutSuccess: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    // 👇 Solo inyectamos el SettingsManager
    private val settingsManager: SettingsManager
) : ViewModel() {

    var uiState by mutableStateOf(SettingsUiState())
        private set

    val isBiometricsEnabled = settingsManager.isBiometricAuthEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun logout() {
        authRepository.logout()
        uiState = uiState.copy(logoutSuccess = true)
    }

    //  La única función que necesitamos es para guardar la preferencia
    fun setBiometricsEnabled(enabled: Boolean) {
        settingsManager.setBiometricAuthEnabled(enabled)
    }
}
