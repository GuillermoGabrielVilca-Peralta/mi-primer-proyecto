// ruta: app/src/main/java/com/tuempresa/driverapp/ui/screens/TripDetailViewModel.kt
package com.tuempresa.driverapp.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.local.models.TripEvent
import com.tuempresa.driverapp.data.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    repo: DriverRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Obtiene el tripId que se le pasa a la pantalla a través de la navegación.
    private val tripId: String = savedStateHandle.get<String>("tripId")!!

    // Llama al repositorio para obtener el Flow de eventos de ese viaje.
    val tripEvents: StateFlow<List<TripEvent>> = repo.getEventsForTrip(tripId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
