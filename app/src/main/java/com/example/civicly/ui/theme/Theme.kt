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
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
)

private val CiviclyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun CiviclyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CiviclyColors,
        typography = Typography,
        shapes = CiviclyShapes,
        content = content,
    )
}
