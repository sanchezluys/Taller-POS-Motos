package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = Color(0xFF00382E),
    primaryContainer = TealContainer,
    onPrimaryContainer = OnTealContainer,
    secondary = AmberSecondary,
    onSecondary = Color(0xFF451A00),
    secondaryContainer = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    tertiary = SkyTertiary,
    onTertiary = Color(0xFF003544),
    tertiaryContainer = SkyContainer,
    onTertiaryContainer = OnSkyContainer,
    background = WorkshopSlateBg,
    onBackground = TextPrimary,
    surface = WorkshopSurface,
    onSurface = TextPrimary,
    surfaceVariant = WorkshopSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = WorkshopBorder,
    error = RedError,
    onError = Color.White,
    errorContainer = RedContainer,
    onErrorContainer = Color(0xFFFCA5A5)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0F766E), // Deep rich teal
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF115E59),
    secondary = Color(0xFFD97706), // Warm amber
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = Color(0xFF0284C7), // Sky blue
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2FE),
    onTertiaryContainer = Color(0xFF075985),
    background = Color(0xFFF1F5F9), // Slate 100
    onBackground = Color(0xFF0F172A), // Slate 900
    surface = Color(0xFFFFFFFF), // Crisp clean white cards
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF8FAFC), // Slate 50
    onSurfaceVariant = Color(0xFF475569), // Slate 600
    outline = Color(0xFFCBD5E1), // Slate 300
    error = RedError,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val bgArgb = if (darkTheme) WorkshopSlateBg.toArgb() else Color(0xFFF1F5F9).toArgb()
            val navArgb = if (darkTheme) WorkshopSurface.toArgb() else Color(0xFFFFFFFF).toArgb()
            window.statusBarColor = bgArgb
            window.navigationBarColor = navArgb
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
