// ruta: C:/Users/Nancy/AndroidStudioProjects/DriverApp/app/src/main/java/com/tuempresa/driverapp/ui/screens/HistoryScreen.kt.
package com.tuempresa.driverapp.ui.screens

// 👇 1. IMPORTA 'clickable' PARA HACER LAS TARJETAS INTERACTIVAS
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuempresa.driverapp.data.local.models.Trip
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    // 👇 2. AÑADE UN PARÁMETRO PARA RECIBIR LA ACCIÓN DE NAVEGACIÓN
    // Este lambda recibirá el ID del viaje que fue pulsado.
    onTripClick: (String) -> Unit
) {
    val trips by viewModel.allTrips.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Historial de Viajes") }) }) { padding ->
        if (trips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no has realizado ningún viaje.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = trips, key = { trip -> trip.id }) { trip ->
                    // 👇 3. PASA LA ACCIÓN DE CLICK A LA TARJETA
                    TripCard(
                        trip = trip,
                        onClick = { onTripClick(trip.id) } // Cuando se pulse, llama al lambda con el ID del viaje
                    )
                }
            }
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    // 👇 4. AÑADE EL PARÁMETRO 'onClick' A LA TARJETA
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // 👇 5. APLICA EL MODIFICADOR 'clickable'
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // El contenido de la tarjeta no cambia en absoluto.
        Column(modifier = Modifier.padding(16.dp)) {
            val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(trip.startTs))

            Text(
                text = "Viaje del: $formattedDate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("Puntaje obtenido: ${trip.finalScore}")
            Text("Duración: ${trip.timeActiveSeconds / 60} minutos")
        }
    }
}
