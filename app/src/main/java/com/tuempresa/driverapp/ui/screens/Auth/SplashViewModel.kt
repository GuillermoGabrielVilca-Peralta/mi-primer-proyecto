package com.tuempresa.driverapp.ui.screens.Auth // O donde corresponda

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    // El estado puede ser:
    // null -> Comprobando...
    // true -> Usuario logueado
    // false -> Usuario NO logueado
    var isUserLoggedIn by mutableStateOf<Boolean?>(null)
        private set

    init {
        viewModelScope.launch {
            isUserLoggedIn = authRepository.getUser().first() != null
        }
    }
}
    