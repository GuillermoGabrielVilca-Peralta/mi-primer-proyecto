// En RewardsScreen.kt
package com.tuempresa.driverapp.ui.screens // Asegúrate de que el paquete sea el correcto

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(viewModel: RewardsViewModel = hiltViewModel()) {
    // Observa el estado del RewardsViewModel. Se recompone automáticamente si el estado cambia.
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Puntuación") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tu Puntuación Actual",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Muestra la puntuación del uiState
            Text(
                text = "${uiState.score}",
                fontSize = 90.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Muestra el rango del uiState
            Text(
                text = "Rango: ${uiState.rank}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("¡Sigue conduciendo de forma segura para mejorar tu rango!")
        }
    }
}
