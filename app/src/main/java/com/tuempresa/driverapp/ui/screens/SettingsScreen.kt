package com.tuempresa.driverapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    // Estados de ejemplo para los interruptores. En una app real, vendrían de un ViewModel o SharedPreferences.
    var useBiometrics by remember { mutableStateOf(true) }
    var autoSync by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Sección de Cuenta
            item {
                SettingsHeader("Cuenta")
                SettingsItem(
                    title = "Editar Perfil",
                    subtitle = "Actualiza tu nombre, foto y vehículo",
                    icon = Icons.Default.Person,
                    onClick = { /* TODO: Navegar a la pantalla de perfil */ }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsSwitchItem(
                    title = "Acceso con huella",
                    subtitle = "Usa tu huella para iniciar sesión",
                    icon = Icons.Default.Fingerprint,
                    checked = useBiometrics,
                    onCheckedChange = { useBiometrics = it }
                )
            }

            // Separador entre secciones
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Sección de Sincronización
            item {
                SettingsHeader("Sincronización de Datos")
                SettingsSwitchItem(
                    title = "Sincronización automática",
                    subtitle = "Sube los viajes automáticamente",
                    icon = Icons.Default.Sync,
                    checked = autoSync,
                    onCheckedChange = { autoSync = it }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    title = "Forzar sincronización",
                    subtitle = "Sube todos los viajes pendientes ahora",
                    icon = Icons.Default.CloudUpload,
                    onClick = { /* TODO: Llamar a la función de sincronización del ViewModel */ }
                )
            }

            // Separador entre secciones
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Sección de Salida
            item {
                SettingsItem(
                    title = "Cerrar Sesión",
                    subtitle = "Finalizarás tu sesión actual en este dispositivo",
                    icon = Icons.Default.Logout,
                    titleColor = MaterialTheme.colorScheme.error, // Color distintivo para acción peligrosa
                    onClick = { /* TODO: Implementar lógica de cerrar sesión */ }
                )
            }
        }
    }
}

// Composable para los encabezados de sección
@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    )
}

// Composable para una fila de ajuste estándar (navegación)
@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    titleColor: androidx.compose.ui.graphics.Color = LocalContentColor.current,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = titleColor)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// Composable para una fila de ajuste con un interruptor
@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) } // Permite hacer clic en toda la fila
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
