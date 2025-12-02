// ruta: C:/Users/Nancy/AndroidStudioProjects/DriverApp/app/src/main/java/com/tuempresa/driverapp/MainActivity.kt.
package com.tuempresa.driverapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // <-- IMPORT para la Splash Screen
import com.tuempresa.driverapp.ui.navigation.NavGraph // <-- CORRECCIÓN: Usa tu ruta y nombre correctos
import com.tuempresa.driverapp.ui.theme.DriverAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val isDataReady = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalar la Splash Screen. DEBE ser lo primero en onCreate.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Mantener la splash screen visible hasta que los datos estén listos.
        splashScreen.setKeepOnScreenCondition {
            // Mientras esto devuelva 'false', la splash seguirá visible.
            !isDataReady.get()
        }

        // Simular la carga de datos en segundo plano (ej. verificar sesión, cargar configs)
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MainActivity", "Simulando carga de datos...")
            // Simulamos una carga de 2.5 segundos. Este es el tiempo que tu animación estará visible.
            delay(2500)

            Log.d("MainActivity", "Datos cargados. La app está lista para mostrarse.")
            isDataReady.set(true)
        }

        // Establecer el contenido de la app con Jetpack Compose.
        // Esto se prepara en segundo plano mientras la Splash Screen está visible.
        setContent {
            DriverAppTheme {
                // CORRECCIÓN: Usamos el nombre original de tu Composable
                NavGraph()
            }
        }
    }
}
