package com.tuempresa.driverapp.ui.screens.Auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Iniciar sesión") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") })
            Spacer(Modifier.height(16.dp))
            Button(onClick = { onLoginSuccess() }, modifier = Modifier.fillMaxWidth()) {
                Text("Entrar")
            }
        }
    }
}
