// ruta: app/src/main/java/com/tuempresa/driverapp/data/managers/SettingsManager.kt
package com.tuempresa.driverapp.data.managers

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("DriverAppSettings", Context.MODE_PRIVATE)

    // Usamos un Flow para que la UI pueda reaccionar a los cambios en tiempo real.
    private val _isBiometricAuthEnabled = MutableStateFlow(prefs.getBoolean(KEY_BIOMETRICS_ENABLED, false))
    val isBiometricAuthEnabled: StateFlow<Boolean> = _isBiometricAuthEnabled

    // Función para actualizar la preferencia
    fun setBiometricAuthEnabled(isEnabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRICS_ENABLED, isEnabled).apply()
        _isBiometricAuthEnabled.value = isEnabled
    }

    companion object {
        private const val KEY_BIOMETRICS_ENABLED = "biometrics_enabled"
    }
}
