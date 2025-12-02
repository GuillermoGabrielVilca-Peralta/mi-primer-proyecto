// ruta: C:/Users/Nancy/AndroidStudioProjects/DriverApp/app/src/main/java/com/tuempresa/driverapp/ui/screens/Auth/LoginScreen.kt
package com.tuempresa.driverapp.ui.screens.Auth

import android.util.Log
import androidx.compose.foundation.Image // <-- 1. AÑADE ESTE IMPORT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // <-- 2. AÑADE ESTE IMPORT
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuempresa.driverapp.R // <-- 3. AÑADE ESTE IMPORT
import com.tuempresa.driverapp.data.managers.BiometricAuthManager
import com.tuempresa.driverapp.data.managers.BiometricAuthStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val uiState = viewModel.uiState
    var passwordVisible by remember { mutableStateOf(false) }

    // (La lógica de la biometría no cambia)
    val context = LocalContext.current
    val biometricAuthManager = remember { BiometricAuthManager(context) }
    val isBiometricsEnabled by viewModel.isBiometricsEnabled.collectAsState(initial = false)

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Iniciar Sesión") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 👇 4. SECCIÓN DEL LOGO AÑADIDA ---
            Image(
                // CAMBIA 'mi_logo' por el nombre de tu archivo de imagen
                // LÍNEA NUEVA (CORRECTA)
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de la App",
                modifier = Modifier
                    .fillMaxWidth(0.5f) // El logo ocupará el 50% del ancho
                    .padding(bottom = 24.dp)
            )
            // --- FIN DE LA SECCIÓN DEL LOGO ---

            Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))

            // --- Tu código de Email (sin cambios) ---
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            // --- Tu código de Contraseña (sin cambios) ---
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )
            Spacer(Modifier.height(24.dp))

            // --- Tu botón de Entrar (sin cambios) ---
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Entrar")
                }
            }

            // --- Tu botón de Registrarse (sin cambios) ---
            TextButton(onClick = onNavigateToSignUp) {
                Text("¿No tienes cuenta? Regístrate")
            }

            // --- Tu sección de la huella (sin cambios) ---
            if (isBiometricsEnabled) {
                Spacer(Modifier.height(24.dp))
                Divider()
                Spacer(Modifier.height(16.dp))
                Text("o usa tu huella para acceder")
                Spacer(Modifier.height(8.dp))

                IconButton(
                    modifier = Modifier.size(64.dp),
                    onClick = {
                        biometricAuthManager.showBiometricPrompt(
                            title = "Inicio de Sesión Biométrico",
                            subtitle = "Usa tu huella para acceder a la app",
                            onResult = { status ->
                                if (status == BiometricAuthStatus.SUCCESS) {
                                    Log.d("LoginScreen", "Autenticación con huella exitosa.")
                                    onLoginSuccess()
                                } else {
                                    Log.d("LoginScreen", "Autenticación con huella fallida o cancelada.")
                                }
                            }
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Acceso con huella",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // --- Tu AlertDialog de error (sin cambios) ---
            if (uiState.error != null) {
                AlertDialog(
                    onDismissRequest = viewModel::onErrorDismissed,
                    title = { Text("Error") },
                    text = { Text(uiState.error) },
                    confirmButton = {
                        TextButton(onClick = viewModel::onErrorDismissed) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}
