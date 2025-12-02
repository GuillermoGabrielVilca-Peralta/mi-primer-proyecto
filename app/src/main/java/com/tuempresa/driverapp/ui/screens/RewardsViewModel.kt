package com.tuempresa.driverapp.ui.screens

// En RewardsViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.driverapp.data.local.db.dao.ScoreDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Estado de la UI para la pantalla de recompensas
data class RewardsUiState(
    val score: Int = 100, // Valor inicial por defecto
    val rank: String = "Novato"
)

@HiltViewModel
class RewardsViewModel @Inject constructor(
    scoreDao: ScoreDao // Hilt inyecta el DAO que configuramos
) : ViewModel() {

    val uiState: StateFlow<RewardsUiState> = scoreDao.getScoreFlow()
        .map { driverScore ->
            val score = driverScore?.score ?: 100
            // Cuando la puntuación cambie, crea un nuevo estado de UI
            RewardsUiState(
                score = score,
                rank = getRankForScore(score) // Calcula el rango basado en la puntuación
            )
        }
        .stateIn(
            scope = viewModelScope,
            // Empieza a escuchar 5 segundos después de que la pantalla no sea visible
            started = SharingStarted.WhileSubscribed(5000),
            // Valor inicial mientras se cargan los datos
            initialValue = RewardsUiState()
        )

    // Función privada para determinar el rango del conductor
    private fun getRankForScore(score: Int): String {
        return when {
            score >= 500 -> "Conductor Élite ★★★"
            score >= 300 -> "Profesional ★★"
            score >= 150 -> "Experimentado ★"
            score < 50 -> "En Observación"
            else -> "Novato"
        }
    }
}
