package com.crome.forecastpoint.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF0288D1)
val BackgroundDark = Color(0xFF1B262C)
val SurfaceDark = Color(0xFF263238)
val SurfaceElevated = Color(0xFF2E3A41)
val OnSurface = Color(0xFFFFFFFF)
val OnSurfaceMuted = Color(0xFFB0BEC5)
val HighTemp = Color(0xFFEF9A9A)
val LowTemp = Color(0xFF90CAF9)
val Amber = Color(0xFFFFCC80)

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    background = BackgroundDark,
    onBackground = OnSurface,
    surface = SurfaceDark,
    onSurface = OnSurface,
    secondary = Amber,
    onSecondary = Color.Black,
)

@Composable
fun ForecastPointTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
