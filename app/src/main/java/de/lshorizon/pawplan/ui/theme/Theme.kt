package de.lshorizon.pawplan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    // Brand
    primary = PrimaryBlue,
    onPrimary = LightBackground,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = LightBackground,
    secondary = SecondaryGreen,
    onSecondary = LightBackground,
    secondaryContainer = SecondaryGreen,
    onSecondaryContainer = LightBackground,
    tertiary = AccentOrange,
    onTertiary = LightBackground,
    tertiaryContainer = AccentOrange,
    onTertiaryContainer = LightBackground,
    // Neutrals
    background = LightBackground,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
)

private val DarkColorScheme = darkColorScheme(
    // Brand
    primary = PrimaryBlue,
    onPrimary = DarkBackground,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = DarkBackground,
    secondary = SecondaryGreen,
    onSecondary = DarkBackground,
    secondaryContainer = SecondaryGreen,
    onSecondaryContainer = DarkBackground,
    tertiary = AccentOrange,
    onTertiary = DarkBackground,
    tertiaryContainer = AccentOrange,
    onTertiaryContainer = DarkBackground,
    // Neutrals
    background = DarkBackground,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
)

@Composable
fun PawPlanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            run { window.statusBarColor = colorScheme.background.toArgb() }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
