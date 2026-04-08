package com.minor.expensetracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = DarkBackground,
    primaryContainer = TealDark,
    onPrimaryContainer = Teal80,
    secondary = Teal60,
    onSecondary = DarkBackground,
    secondaryContainer = DarkCardElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = GradientPurpleEnd,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkInputField,
    outlineVariant = DarkCard,
    error = ExpenseRed,
    onError = TextWhite,
    inverseSurface = LightSurface,
    inverseOnSurface = TextDark,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkCard,
    surfaceContainer = DarkCardElevated,
    surfaceContainerHigh = DarkInputField,
    surfaceContainerHighest = Color(0xFF303050),
)

private val LightColorScheme = lightColorScheme(
    primary = TealDark,
    onPrimary = TextWhite,
    primaryContainer = Color(0xFFB2F0DC),
    onPrimaryContainer = Color(0xFF004D36),
    secondary = Teal40,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFFD4F5E9),
    onSecondaryContainer = Color(0xFF004D36),
    tertiary = GradientPurpleStart,
    onTertiary = TextWhite,
    background = LightBackground,
    onBackground = Color(0xFF0F172A),       // near-black for readable text
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),    // slate-600, good secondary text
    outline = Color(0xFFCBD5E1),             // slate-300
    outlineVariant = Color(0xFFE2E8F0),      // slate-200
    error = Color(0xFFDC2626),
    onError = TextWhite,
    inverseSurface = DarkSurface,
    inverseOnSurface = TextPrimary,
    surfaceContainerLowest = LightBackground,
    surfaceContainerLow = LightCard,
    surfaceContainer = LightCardElevated,
    surfaceContainerHigh = LightInputField,
    surfaceContainerHighest = Color(0xFFDDE3EE),
)

// Global theme state for manual toggle
object ThemeState {
    var isDarkMode by mutableStateOf(true) // Dark-first as per Figma
}

// Gradient helpers
object AppGradients {
    val cardGradient: Brush
        @Composable get() = Brush.linearGradient(
            colors = listOf(GradientCardStart, GradientCardEnd, GradientTealEnd)
        )

    val tealGradient: Brush
        @Composable get() = Brush.linearGradient(
            colors = listOf(GradientTealStart, GradientTealMid, GradientTealEnd)
        )

    val purpleGradient: Brush
        @Composable get() = Brush.linearGradient(
            colors = listOf(GradientPurpleStart, GradientPurpleEnd)
        )

    val incomeGradient: Brush
        @Composable get() = Brush.horizontalGradient(
            colors = listOf(IncomeGreen, Teal60)
        )

    val expenseGradient: Brush
        @Composable get() = Brush.horizontalGradient(
            colors = listOf(ExpenseRed, Color(0xFFFF8A80))
        )
}

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = ThemeState.isDarkMode,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}