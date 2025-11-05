package com.tuempresa.driverapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun RewardsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recompensas") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Aquí se mostrarán las recompensas del conductor.")
        }
    }
}
