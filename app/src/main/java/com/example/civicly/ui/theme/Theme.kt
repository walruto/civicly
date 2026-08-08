package com.example.civicly.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val CiviclyColors = lightColorScheme(
    primary = SlateNavy,
    onPrimary = SurfaceWhite,
    primaryContainer = SlateNavyDeep,
    onPrimaryContainer = SurfaceWhite,
    secondary = DeepCobalt,
    onSecondary = SurfaceWhite,
    tertiary = OchreAmber,
    onTertiary = SurfaceWhite,
    background = CoolGrayCanvas,
    onBackground = OnSurface,
    surface = SurfaceWhite,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
)

// DESIGN.md: 4px on controls, 8px on cards/hero.
private val CiviclyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp),
)

// ponytail: static light scheme only. Add dark/dynamic when product asks.
@Composable
fun CiviclyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CiviclyColors,
        typography = Typography,
        shapes = CiviclyShapes,
        content = content,
    )
}
