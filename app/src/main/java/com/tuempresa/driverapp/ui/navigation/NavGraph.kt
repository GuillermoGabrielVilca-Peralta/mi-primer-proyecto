package com.tuempresa.driverapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tuempresa.driverapp.ui.screens.*
import com.tuempresa.driverapp.ui.screens.Auth.LoginScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = { navController.navigate("dashboard") })
        }

        composable("dashboard") {
            DashboardScreen(
                onStartTrip = { navController.navigate("trip") },
                onOpenHistory = { navController.navigate("history") },
                onOpenRewards = { navController.navigate("rewards") },
                onOpenSettings = { navController.navigate("settings") }
            )
        }

        composable("trip") {
            TripScreen(onFinishTrip = { navController.navigate("dashboard") })
        }

        composable("history") { HistoryScreen() }
        composable("rewards") { RewardsScreen() }
        composable("settings") { SettingsScreen() }
    }
}
