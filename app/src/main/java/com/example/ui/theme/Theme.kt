package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SawtColorScheme = darkColorScheme(
    primary = MoroccanRed,
    onPrimary = Color.White,
    primaryContainer = MoroccanRedDark,
    onPrimaryContainer = Color.White,
    secondary = MoroccanGreen,
    onSecondary = Color.White,
    secondaryContainer = MoroccanGreenDark,
    onSecondaryContainer = Color.White,
    tertiary = MoroccanGold,
    onTertiary = Color.Black,
    background = SawtObsidian,
    onBackground = SawtTextPrimary,
    surface = SawtSurface,
    onSurface = SawtTextPrimary,
    surfaceVariant = SawtCard,
    onSurfaceVariant = SawtTextSecondary,
    outline = SawtCardBorder,
    error = StatusError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SawtColorScheme,
        typography = Typography,
        content = content
    )
}
