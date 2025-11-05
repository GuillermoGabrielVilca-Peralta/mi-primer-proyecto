package com.tuempresa.driverapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

// 👇 AGREGA ESTOS IMPORTS FALTANTES
import com.tuempresa.driverapp.data.repository.DriverRepository
import com.tuempresa.driverapp.data.local.models.Trip
import com.tuempresa.driverapp.data.local.models.TripEvent


@HiltViewModel
class TripViewModel @Inject constructor(private val repo: DriverRepository): ViewModel() {
    val currentScore = MutableStateFlow(100)

    fun saveTrip(trip: Trip) = viewModelScope.launch {
        repo.saveTripLocal(trip)
    }

    fun calculateScore(events: List<TripEvent>, excesoPromKmh: Double): Int {
        var score = 100
        events.forEach { e ->
            when(e.type) {
                "frenada" -> score -= 10 * e.severity
                "acel_br" -> score -= 5 * e.severity
                "uso_cel" -> {
                    val sec = JSONObject(e.metadata).optInt("duration",0)
                    score -= 20 * (sec / 10)
                }
                "somnolencia" -> score -= 30 * e.severity
                "salida_carril" -> score -= 15 * e.severity
                "rojo" -> score -= 25
            }
        }
        score -= (excesoPromKmh * 0.5).toInt()
        if (events.isEmpty()) score += 20
        return score.coerceIn(0,100)
    }
}
