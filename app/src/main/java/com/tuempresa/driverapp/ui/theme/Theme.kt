package com.tuempresa.driverapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta para el modo claro
private val LightColorScheme = lightColorScheme(
    primary = WarmGreenPrimary,
    onPrimary = WarmGreenOnPrimary,
    primaryContainer = WarmGreenContainer,
    onPrimaryContainer = Color(0xFF002107),

    secondary = WarmGreenSecondary,
    onSecondary = WarmGreenOnSecondary,
    secondaryContainer = Color(0xFFDEE8D9),
    onSecondaryContainer = Color(0xFF181F18),

    tertiary = AccentOrange,
    onTertiary = AccentOnOrange,
    tertiaryContainer = Color(0xFFFFDDBA),
    onTertiaryContainer = Color(0xFF2C1600),

    background = LightBackground,
    onBackground = OnLightSurface,
    surface = LightSurface,
    onSurface = OnLightSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF424940)
)

// (Puedes definir una DarkColorScheme aquí si quieres, por ahora nos centramos en la clara)

@Composable
fun DriverAppTheme(
    darkTheme: Boolean = false, // isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        // DarkColorScheme (aún no definida)
        LightColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de que tu Typography esté definida
        content = content
    )
}
