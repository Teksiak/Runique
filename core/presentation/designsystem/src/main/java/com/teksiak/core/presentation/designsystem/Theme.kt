package com.teksiak.core.presentation.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = RuniqueGreen,
    background = RuniqueBlack,
    surface = RuniqueDarkGray,
    secondary = RuniqueWhite,
    tertiary = RuniqueWhite,
    primaryContainer = RuniqueGreen30,
    onPrimary = RuniqueBlack,
    onBackground = RuniqueWhite,
    onSurface = RuniqueWhite,
    onSurfaceVariant = RuniqueGray,
    surfaceVariant = RuniqueGray,
    surfaceContainer = RuniqueDarkGreen,
    error = RuniqueDarkRed,
)

val LightColorScheme = darkColorScheme(
    primary = RuniqueBlue,
    background = RuniqueWhite,
    surface = RuniqueGray5,
    secondary = RuniqueBlack,
    tertiary = RuniqueBlack,
    primaryContainer = RuniqueBlue30,
    onPrimary = RuniqueWhite,
    onBackground = RuniqueBlack,
    onSurface = RuniqueBlack,
    onSurfaceVariant = RuniqueGray,
    surfaceVariant = RuniqueGray40,
    surfaceContainer = RuniqueLightBlue,
    error = RuniqueDarkRed,
)

@Composable
fun RuniqueTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}