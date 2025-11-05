package com.tuempresa.driverapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.local.models.Trip
import com.tuempresa.driverapp.data.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: DriverRepository // 1. Hilt inyecta el repositorio.
) : ViewModel() {

    // 2. Obtenemos el Flow de viajes y lo convertimos en un StateFlow.
    //    La UI podrá observar este StateFlow de forma segura.
    val allTrips: StateFlow<List<Trip>> = repository.getAllTrips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Empieza a escuchar cuando la UI está visible
            initialValue = emptyList() // El valor inicial mientras se cargan los datos
        )
}
