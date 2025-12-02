// ruta: C:/Users/Nancy/AndroidStudioProjects/DriverApp/app/src/main/java/com/tuempresa/driverapp/ui/navigation/NavGraph.kt.
package com.tuempresa.driverapp.ui.navigation

// ... (imports de foundation, material3, etc. no cambian) ...
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
// 👇 1. IMPORTA NavType y navArgument PARA MANEJAR PARÁMETROS
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation

// ... (tus importaciones de Screens existentes no cambian) ...
import com.tuempresa.driverapp.ui.screens.DashboardScreen
import com.tuempresa.driverapp.ui.screens.HistoryScreen
import com.tuempresa.driverapp.ui.screens.MapScreen
import com.tuempresa.driverapp.ui.screens.RewardsScreen
import com.tuempresa.driverapp.ui.screens.SettingsScreen
import com.tuempresa.driverapp.ui.screens.TripScreen
import com.tuempresa.driverapp.ui.screens.Auth.LoginScreen
import com.tuempresa.driverapp.ui.screens.Auth.SignUpScreen
import com.tuempresa.driverapp.ui.screens.Auth.SplashViewModel
import com.tuempresa.driverapp.ui.screens.EditProfileScreen
// 👇 2. IMPORTA LA NUEVA PANTALLA DE DETALLE DEL VIAJE
import com.tuempresa.driverapp.ui.screens.TripDetailScreen

object AuthRoutes {
    // ... (sin cambios)
    const val ROOT = "auth_root"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
}

object MainRoutes {
    // ... (sin cambios en las rutas existentes)
    const val ROOT = "main_root"
    const val DASHBOARD = "dashboard"
    const val TRIP = "trip"
    const val HISTORY = "history"
    const val REWARDS = "rewards"
    const val SETTINGS = "settings"
    const val MAP = "map"
    const val EDIT_PROFILE = "edit_profile"
    // 👇 3. AÑADE UNA CONSTANTE PARA LA NUEVA RUTA
    const val TRIP_DETAIL = "trip_detail" // Ruta base para el detalle
}

@Composable
fun NavGraph() {
    // ... (esta función no necesita cambios) ...
    val navController = rememberNavController()
    val splashViewModel: SplashViewModel = hiltViewModel()
    val isUserLoggedIn = splashViewModel.isUserLoggedIn

    if (isUserLoggedIn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (isUserLoggedIn) MainRoutes.ROOT else AuthRoutes.ROOT

    NavHost(navController = navController, startDestination = startDestination) {
        authGraph(navController)
        mainGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavController) {
    // ... (este grafo no necesita cambios) ...
    navigation(startDestination = AuthRoutes.LOGIN, route = AuthRoutes.ROOT) {
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(MainRoutes.ROOT) {
                        popUpTo(AuthRoutes.ROOT) { inclusive = true }
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
                    navController.navigate(MainRoutes.ROOT) {
                        popUpTo(AuthRoutes.ROOT) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}

private fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(startDestination = MainRoutes.DASHBOARD, route = MainRoutes.ROOT) {
        // ... (composable de DASHBOARD, TRIP, MAP, REWARDS no cambian) ...
        composable(MainRoutes.DASHBOARD) {
            DashboardScreen(
                onStartTrip = { navController.navigate(MainRoutes.TRIP) },
                onOpenHistory = { navController.navigate(MainRoutes.HISTORY) },
                onOpenRewards = { navController.navigate(MainRoutes.REWARDS) },
                onOpenSettings = {
                    navController.navigate(MainRoutes.SETTINGS)
                },
                onOpenMap = { navController.navigate(MainRoutes.MAP) }
            )
        }
        composable(MainRoutes.TRIP) { TripScreen(onFinishTrip = { navController.popBackStack() }) }
        composable(MainRoutes.MAP) { MapScreen() }
        composable(MainRoutes.REWARDS) { RewardsScreen() }

        // 👇 4. MODIFICA EL COMPOSABLE DE HISTORY
        composable(MainRoutes.HISTORY) {
            // Le pasas la acción que debe ejecutar cuando se hace clic en un viaje
            HistoryScreen(
                onTripClick = { tripId ->
                    // Navega a la nueva ruta, pasando el ID del viaje
                    navController.navigate("${MainRoutes.TRIP_DETAIL}/$tripId")
                }
            )
        }

        // ... (composable de SETTINGS no cambia) ...
        composable(MainRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateToEditProfile = {
                    navController.navigate(MainRoutes.EDIT_PROFILE)
                },
                onLogoutSuccess = {
                    navController.navigate(AuthRoutes.ROOT) {
                        popUpTo(MainRoutes.ROOT) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(MainRoutes.EDIT_PROFILE) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 👇 5. AÑADE EL NUEVO COMPOSABLE PARA LA PANTALLA DE DETALLE
        composable(
            // La ruta completa es "trip_detail/{tripId}"
            route = "${MainRoutes.TRIP_DETAIL}/{tripId}",
            // Le decimos al sistema de navegación que esperamos un argumento llamado "tripId"
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            // Cuando se navega a esta ruta, se muestra la pantalla de detalle.
            // El ViewModel se encargará de obtener el 'tripId' automáticamente.
            TripDetailScreen(
                onNavigateBack = {
                    navController.popBackStack() // Para el botón de "atrás"
                }
            )
        }
    }
}
