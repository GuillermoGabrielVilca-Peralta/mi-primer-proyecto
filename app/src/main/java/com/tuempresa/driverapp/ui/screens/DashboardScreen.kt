package com.tuempresa.driverapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuempresa.driverapp.ui.theme.DriverAppTheme // Asegúrate que la ruta a tu tema sea correcta

// Data class para definir cada opción del menú
data class DashboardAction(
    val title: String,
    val icon: ImageVector,
    val action: () -> Unit,
    val isFeatured: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onStartTrip: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenRewards: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMap: () -> Unit
) {
    // 1. Definimos una lista de acciones para que sea fácil de modificar
    val actions = listOf(
        DashboardAction("Historial", Icons.Default.History, onOpenHistory),
        DashboardAction("Recompensas", Icons.Default.WorkspacePremium, onOpenRewards),
        DashboardAction("Mapa", Icons.Default.Map, onOpenMap),
        DashboardAction("Ajustes", Icons.Default.Settings, onOpenSettings)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Conductor") },
                colors = TopAppBarDefaults.topAppBarColors(
                    //  CAMBIO 1: Usa el color primario para el fondo de la barra
                    containerColor = MaterialTheme.colorScheme.primary,

                    //  CAMBIO 2: Usa el color "sobre primario" para el texto
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2. Tarjeta principal para la acción más importante: "Iniciar Viaje"
            Card(
                onClick = onStartTrip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp), // Mayor altura para destacar
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Iniciar Viaje",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Iniciar Viaje",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // 3. Grid para las acciones secundarias
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Dos columnas
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(actions) { item ->
                    DashboardCard(item = item)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(item: DashboardAction) {
    Card(
        onClick = item.action,
        modifier = Modifier
            .aspectRatio(1f), // Para que sea un cuadrado perfecto
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Preview para ver el diseño sin ejecutar la app
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DriverAppTheme { // Usa tu tema principal
        DashboardScreen({}, {}, {}, {}, {})
    }
}
