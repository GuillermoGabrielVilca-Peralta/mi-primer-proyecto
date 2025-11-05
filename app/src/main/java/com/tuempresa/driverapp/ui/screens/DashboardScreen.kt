package com.tuempresa.driverapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onStartTrip: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenRewards: () -> Unit,
    onOpenSettings: () -> Unit
    // ELIMINAMOS onOpenRoutes de aquí
) {
    Scaffold(topBar = { TopAppBar(title = { Text("INICIO") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Estado de conducción: OK", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))

            Button(onClick = onStartTrip, modifier = Modifier.fillMaxWidth()) { Text("Iniciar viaje") }
            Button(onClick = onOpenHistory, modifier = Modifier.fillMaxWidth()) { Text("Historial") }
            Button(onClick = onOpenRewards, modifier = Modifier.fillMaxWidth()) { Text("Recompensas") }

            // ELIMINAMOS el botón de "Mis Rutas (CRUD)" de aquí

            Button(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) { Text("Ajustes") }
        }
    }
}
