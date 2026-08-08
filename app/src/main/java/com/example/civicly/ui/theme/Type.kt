package com.example.civicly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Sizes/weights from DESIGN.md. Inter typeface skipped — needs ui-text-google-fonts.
// Add Inter when brand-fidelity beats zero dependencies.
private val Sans = FontFamily.Default

private fun style(
    weight: FontWeight,
    size: Int,
    line: Int,
    tracking: Double = 0.0,
) = TextStyle(
    fontFamily = Sans,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.sp,
)

val Typography = Typography(
    displayLarge = style(FontWeight.Bold, 28, 34, -0.28),
    headlineMedium = style(FontWeight.SemiBold, 24, 32, -0.24),
    headlineSmall = style(FontWeight.SemiBold, 20, 28),
    titleLarge = style(FontWeight.SemiBold, 22, 28),
    titleMedium = style(FontWeight.SemiBold, 18, 24),
    titleSmall = style(FontWeight.SemiBold, 16, 20),
    bodyLarge = style(FontWeight.Normal, 18, 28),
    bodyMedium = style(FontWeight.Normal, 16, 24),
    bodySmall = style(FontWeight.Normal, 14, 20),
    labelMedium = style(FontWeight.SemiBold, 12, 16, 0.6),
    labelSmall = style(FontWeight.Medium, 11, 14),
)
