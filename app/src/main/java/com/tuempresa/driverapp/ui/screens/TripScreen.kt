package com.tuempresa.driverapp.ui.screens

// 1. ELIMINA LOS IMPORTS DE CAMERA, YA NO SE USAN AQUÍ
// import androidx.camera.core.ImageProxy
// import com.tuempresa.driverapp.ui.screens.CameraPreview

// 2. AÑADE LOS IMPORTS NECESARIOS PARA LA NUEVA UI Y EL VIEWMODEL
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuempresa.driverapp.ui.screens.ActiveTripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(
    // 3. INYECTA EL NUEVO VIEWMODEL
    viewModel: ActiveTripViewModel = hiltViewModel(),
    onFinishTrip: () -> Unit
) {
    // 4. OBSERVA EL ESTADO PROVENIENTE DEL VIEWMODEL
    val uiState by viewModel.uiState.collectAsState()

    // 5. ESTE BLOQUE INICIA EL MONITOREO CUANDO LA PANTALLA APARECE
    LaunchedEffect(Unit) {
        viewModel.startTripMonitoring()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Viaje en Curso") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Distribuye el espacio para que no se amontone todo arriba
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // 6. REEMPLAZAMOS LA VISTA DE CÁMARA POR EL INDICADOR DE VELOCIDAD
            SpeedIndicator(speed = uiState.speed)

            // 7. AÑADIMOS EL NUEVO PANEL DE ALERTAS
            AlertPanel(
                message = uiState.alertMessage,
                isCritical = uiState.isCriticalAlert
            )

            // 8. EL BOTÓN SE MANTIENE
            Button(onClick = onFinishTrip, modifier = Modifier.fillMaxWidth()) {
                Text("Finalizar Viaje")
            }
        }
    }
}

// --- NUEVOS COMPOSABLES PARA UNA UI MÁS CLARA ---

@Composable
fun SpeedIndicator(speed: Int) {
    Card(
        modifier = Modifier.size(200.dp),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "$speed",
                fontSize = 80.sp,
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "km/h",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun AlertPanel(message: String, isCritical: Boolean) {
    val backgroundColor = if (isCritical) MaterialTheme.colorScheme.errorContainer
    else MaterialTheme.colorScheme.primaryContainer

    val textColor = if (isCritical) MaterialTheme.colorScheme.onErrorContainer
    else MaterialTheme.colorScheme.onPrimaryContainer

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = message,
                color = textColor,
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }
    }
}
