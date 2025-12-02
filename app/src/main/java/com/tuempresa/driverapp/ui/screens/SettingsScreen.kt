// ruta: C:/Users/Nancy/AndroidStudioProjects/DriverApp/app/src/main/java/com/tuempresa/driverapp/ui/screens/SettingsScreen.kt.
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
import androidx.compose.ui.platform.LocalContext // <-- ¡Importante!
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuempresa.driverapp.data.managers.BiometricAuthManager // <-- ¡Importante!
import com.tuempresa.driverapp.data.managers.BiometricAuthStatus // <-- ¡Importante!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogoutSuccess: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val isBiometricsEnabled by viewModel.isBiometricsEnabled.collectAsState()

    // 👇 1. Obtenemos el contexto actual y creamos el manager aquí
    val context = LocalContext.current
    val biometricAuthManager = remember { BiometricAuthManager(context) }
    val isBiometricAvailable = remember { biometricAuthManager.checkBiometricSupport() }

    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            onLogoutSuccess()
        }
    }

    var autoSync by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes")},colors = TopAppBarDefaults.topAppBarColors(
            // 👇 ESTA LÍNEA ES LA CAUSA
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ) ) }
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
                    onClick = onNavigateToEditProfile
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                // 👇 2. La lógica de visibilidad ahora usa la variable local
                if (isBiometricAvailable) {
                    SettingsSwitchItem(
                        title = "Acceso con huella",
                        subtitle = "Usa tu huella para iniciar sesión",
                        icon = Icons.Default.Fingerprint,
                        checked = isBiometricsEnabled,
                        // 👇 3. La lógica de cambio ahora está aquí en la UI
                        onCheckedChange = { isEnabled ->
                            if (isEnabled) {
                                // Si se activa, muestra el diálogo
                                biometricAuthManager.showBiometricPrompt(
                                    title = "Confirmar para activar",
                                    subtitle = "Usa tu huella para habilitar el acceso biométrico"
                                ) { status ->
                                    if (status == BiometricAuthStatus.SUCCESS) {
                                        // Si es exitoso, le decimos al ViewModel que guarde el estado
                                        viewModel.setBiometricsEnabled(true)
                                    }
                                }
                            } else {
                                // Si se desactiva, simplemente guardamos el estado
                                viewModel.setBiometricsEnabled(false)
                            }
                        }
                    )
                }
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
                    onClick = { /* TODO */ }
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
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { viewModel.logout() }
                )
            }
        }
    }
}

// ======================================================================
// LOS COMPOSABLES DE ABAJO ESTÁN PERFECTOS Y NO NECESITAN NINGÚN CAMBIO
// ======================================================================

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

// En C:/Users/Nancy/AndroidStudioProjects/DriverApp/app/src/main/java/com/tuempresa/driverapp/ui/screens/SettingsScreen.kt.

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        // 👇 1. ELIMINAMOS el .clickable de aquí.
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Dejamos el padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))

        // Colocamos el Column y el Switch dentro de un Row secundario
        // para que el clic solo afecte a esa área y no a todo el espacio.
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // 👇 2. ESTE ES AHORA EL ÚNICO RESPONSABLE DEL CAMBIO
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

