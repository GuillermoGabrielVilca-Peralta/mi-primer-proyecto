// ruta: app/src/main/java/com/tuempresa/driverapp/ui/screens/Auth/SignUpScreen.kt
package com.tuempresa.driverapp.ui.screens.Auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
// 👇 1. IMPORTAMOS LOS ICONOS QUE VAMOS A USAR
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation // Import para el ojito de la contraseña
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState = viewModel.uiState
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.signUpSuccess) {
        if (uiState.signUpSuccess) {
            onSignUpSuccess()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Crear Cuenta") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 👇 2. AÑADIMOS EL CAMPO DE TEXTO PARA EL NOMBRE
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = viewModel::onNameChange, // Llama a la función del ViewModel
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre") },
                singleLine = true, // Evita que se hagan múltiples líneas
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Spacer(Modifier.height(8.dp)) // Espacio entre campos

            // Campo de Correo Electrónico (sin cambios)
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(8.dp))

            // 👇 3. MEJORAMOS EL CAMPO DE CONTRASEÑA CON EL ICONO DE VISIBILIDAD
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar contraseña")
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            // 👇 4. MEJORAMOS EL CAMPO DE CONFIRMAR CONTRASEÑA TAMBIÉN
            OutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar contraseña")
                    }
                }
            )
            Spacer(Modifier.height(24.dp))

            // Botón de registro (sin cambios en la lógica)
            Button(
                onClick = { viewModel.signUp() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Registrarse")
                }
            }
            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }

            // Diálogo de error (sin cambios)
            if (uiState.error != null) {
                AlertDialog(
                    onDismissRequest = viewModel::onErrorDismissed,
                    title = { Text("Error") },
                    text = { Text(uiState.error) },
                    confirmButton = {
                        TextButton(onClick = viewModel::onErrorDismissed) { Text("Aceptar") }
                    }
                )
            }
        }
    }
}
