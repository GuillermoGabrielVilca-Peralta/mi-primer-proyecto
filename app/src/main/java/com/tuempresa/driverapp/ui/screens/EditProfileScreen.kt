// ruta: app/src/main/java/com/tuempresa/driverapp/ui/screens/EditProfileScreen.kt
package com.tuempresa.driverapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val name = viewModel.name

    // Cuando el perfil se actualice con éxito, navega hacia atrás.
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Muestra un indicador de carga mientras se obtienen los datos iniciales.
            if (uiState.isLoading && name.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, "Nombre") },
                    singleLine = true
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.onSaveProfile() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Guardar Cambios")
                    }
                }
            }

            // Manejo de errores
            val error = uiState.loadError ?: uiState.updateError
            if (error != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.onErrorDismissed() },
                    title = { Text("Error") },
                    text = { Text(error) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.onErrorDismissed() }) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}
