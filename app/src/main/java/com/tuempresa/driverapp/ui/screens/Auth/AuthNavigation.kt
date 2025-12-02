// ruta: app/src/main/java/com/tuempresa/driverapp/ui/screens/Auth/AuthNavigation.kt
package com.tuempresa.driverapp.ui.screens.Auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

// Define las rutas para el sub-grafo de autenticación
object AuthRoutes {
    const val AUTH_ROOT = "auth_root" // Ruta raíz del sub-grafo
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
}

// Extensión que añade el grafo de autenticación al navegador principal
fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(
        route = AuthRoutes.AUTH_ROOT,
        startDestination = AuthRoutes.LOGIN
    ) {
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    // Cuando el login es exitoso, navegamos a la pantalla principal
                    // y limpiamos el historial de navegación de autenticación.
                    navController.navigate("main_flow") {
                        popUpTo(AuthRoutes.AUTH_ROOT) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(AuthRoutes.SIGN_UP)
                }
            )
        }
        composable(AuthRoutes.SIGN_UP) {
            SignUpScreen(
                onSignUpSuccess = {
                    // Después de registrarse, navegamos a la pantalla principal
                    navController.navigate("main_flow") {
                        popUpTo(AuthRoutes.AUTH_ROOT) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack() // Vuelve a la pantalla de login
                }
            )
        }
    }
}
    