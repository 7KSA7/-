package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VipDarkColorScheme = darkColorScheme(
    primary = NeonEmerald,
    onPrimary = CyberCanvasDark,
    primaryContainer = TitaniumCard,
    onPrimaryContainer = TextPrimary,
    secondary = CyberCyan,
    onSecondary = CyberCanvasDark,
    tertiary = CrimsonDanger,
    background = CyberCanvasDark,
    onBackground = TextPrimary,
    surface = TitaniumSurface,
    onSurface = TextPrimary,
    surfaceVariant = TitaniumCard,
    onSurfaceVariant = TextSecondary,
    outline = TitaniumBorder,
    error = CrimsonDanger,
    onError = TextPrimary
)

@Composable
fun VipProtectionTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VipDarkColorScheme,
        typography = Typography,
        content = content
    )
}
