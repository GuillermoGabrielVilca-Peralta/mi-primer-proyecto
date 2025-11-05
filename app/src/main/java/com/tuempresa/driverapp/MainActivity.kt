package com.tuempresa.driverapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tuempresa.driverapp.ui.navigation.NavGraph
import com.tuempresa.driverapp.ui.theme.DriverAppTheme
import dagger.hilt.android.AndroidEntryPoint // <-- PASO 1: AÑADE ESTE IMPORT

// 👇 PASO 2: AÑADE ESTA ANOTACIÓN
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DriverAppTheme {
                NavGraph()
            }
        }
    }
}
