package com.ainotes.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun getAppBackgroundGradient(): Brush {
    val isDark = MaterialTheme.colorScheme.background == BackgroundDark
    return if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                BackgroundDark,
                Color(0xFF12122A),
                Color(0xFF080818)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                BackgroundLight,           // soft lavender-white at top
                Color(0xFFF2F0FF),         // slightly deeper lavender
                Color(0xFFFFFFFF)          // pure white at bottom
            )
        )
    }
}

@Composable
fun getCardGradient(isBlue: Boolean = false): Brush {
    return if (isBlue) {
        Brush.linearGradient(colors = listOf(CardBlueStart, CardBlueEnd))
    } else {
        Brush.linearGradient(colors = listOf(CardPurpleStart, CardPurpleEnd))
    }
}

@Composable
fun AiNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
