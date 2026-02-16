package com.applimit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import java.util.Locale

// ========== MATERIAL 3 COLOR SCHEMES (Material You) ==========

private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    
    // Secondary colors
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    
    // Tertiary colors
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    
    // Background and Surface colors
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceDim = SurfaceDim,
    surfaceBright = SurfaceBright,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    
    // Error colors
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    
    // Outline colors
    outline = Outline,
    outlineVariant = OutlineVariant,
    scrim = Scrim,
)

private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF001A40),
    
    // Secondary colors
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = Color(0xFFEDE7FF),
    onSecondaryContainer = Color(0xFF2A1B3D),
    
    // Tertiary colors
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = Color(0xFFE0F7FA),
    onTertiaryContainer = Color(0xFF005F6E),
    
    // Background and Surface colors
    background = Color(0xFFFCFCFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFCFCFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceDim = Color(0xFFDED9E0),
    surfaceBright = Color(0xFFFCFCFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F2F9),
    surfaceContainer = Color(0xFFF1ECF3),
    surfaceContainerHigh = Color(0xFFEBE6ED),
    surfaceContainerHighest = Color(0xFFE6E1E8),
    surfaceVariant = Color(0xFFE7E0EB),
    onSurfaceVariant = Color(0xFF49454E),
    
    // Error colors
    error = Error,
    onError = OnError,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    
    // Outline colors
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC7D0),
    scrim = Color(0xFF000000),
)

// Define the CompositionLocal for the locale.
val LocalLocale = compositionLocalOf { Locale.getDefault() }

// ========== CUSTOM COMPOSITION LOCALS FOR DESIGN TOKENS ==========
val LocalSpacing = compositionLocalOf { Dimensions() }
val LocalShapes = compositionLocalOf { Shapes }

@Composable
fun SafeTimeGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SafeTimeGuardTypography,
        shapes = Shapes,
        content = content
    )
}

