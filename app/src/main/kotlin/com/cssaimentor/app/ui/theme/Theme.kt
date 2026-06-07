package com.cssaimentor.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = MentorCyan,
    onPrimary = MentorBlack,
    secondary = MentorPurple,
    onSecondary = MentorText,
    tertiary = MentorGreen,
    background = MentorBlack,
    onBackground = MentorText,
    surface = MentorSurface,
    onSurface = MentorText,
    surfaceVariant = MentorSurfaceHigh,
    onSurfaceVariant = MentorTextMuted,
    outline = MentorLine,
    error = MentorError
)

@Composable
fun CSSAIMentorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = MentorBlack.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MentorTypography,
        content = content
    )
}

@Composable
fun shouldUseDarkTheme(): Boolean = isSystemInDarkTheme()

