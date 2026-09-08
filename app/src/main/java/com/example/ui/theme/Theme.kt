package com.example.ui.theme

import android.app.Activity
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
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = ElectricPurple,
    onPrimaryContainer = Color.White,
    secondary = VividViolet,
    onSecondary = Color.White,
    secondaryContainer = CyberDarkSurfaceElevated,
    onSecondaryContainer = NeonCyan,
    tertiary = CyberMagenta,
    onTertiary = Color.White,
    background = CyberDarkBg,
    onBackground = TextPrimaryDark,
    surface = CyberDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = CyberDarkSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = CyberCardBorder,
    outlineVariant = Color(0xFF221A45)
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADBFF),
    onPrimaryContainer = VividViolet,
    secondary = BrightBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF1E40AF),
    tertiary = CyberMagenta,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun InnovaAiTheme(
    darkTheme: Boolean = true, // Default to futuristic dark aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
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

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    InnovaAiTheme(darkTheme = true, content = content)
}
