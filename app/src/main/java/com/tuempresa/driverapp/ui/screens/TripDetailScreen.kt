// ruta: app/src/main/java/com/tuempresa/driverapp/ui/screens/TripDetailScreen.kt
package com.tuempresa.driverapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuempresa.driverapp.data.local.models.TripEvent
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val events by viewModel.tripEvents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Viaje") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay eventos de penalización en este viaje. ¡Felicidades!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    EventCard(event = event)
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: TripEvent) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alerta",
                tint = when (event.severity) {
                    in 4..5 -> Color.Red
                    in 2..3 -> Color(0xFFFFA000) // Naranja
                    else -> Color.Gray
                },
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatEventType(event.type),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event.metadata, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = formatTimestamp(event.timestamp), style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
        }
    }
}

private fun formatEventType(type: String): String {
    return type.replace("_", " ").split(' ')
        .joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return format.format(date)
}
