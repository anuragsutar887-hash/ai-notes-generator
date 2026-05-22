package com.ainotes.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    secondary = Secondary,
    onSecondary = OnSecondary,
    tertiary = Tertiary,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = Color(0xFFB0B0D0),
    error = ErrorColor,
    outline = Color(0xFF3D3D60)
)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    tertiary = Tertiary,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = OnSurfaceLightColor,
    onSurface = OnSurfaceLightColor,
    onSurfaceVariant = Color(0xFF6B6B8A),
    error = ErrorColor,
    outline = Color(0xFFE0DDFF)
)
