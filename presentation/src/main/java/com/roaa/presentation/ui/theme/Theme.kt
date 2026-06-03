package com.roaa.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.materialkolor.*
import com.materialkolor.ktx.harmonize


@Immutable
data class CustomColorRoles(
    val solidColor: Color,
    val onSolidColor: Color,
    val solidColorContainer: Color,
    val onSolidColorContainer: Color
)

// Extension function to quickly spit out the tokens based on your app state
@Composable
fun Color.toCustomRoles(isDark: Boolean = isSystemInDarkTheme()): CustomColorRoles {
    val harmonized = this.harmonize(MaterialTheme.colorScheme.onSurface)
    val roles = rememberDynamicColorScheme(this, isDark)

    return CustomColorRoles(
        solidColor = roles.primary,
        onSolidColor = roles.onPrimary,
        solidColorContainer = roles.primaryContainer,
        onSolidColorContainer = roles.onPrimaryContainer
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PasswordManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Expressive palette — drives primary, secondary, tertiary tones
    val expressiveScheme = rememberDynamicColorScheme(
        seedColor = expressiveSeed,
        isDark = darkTheme,
        isAmoled = false,
        style = PaletteStyle.TonalSpot
    )

    // Neutral palette — drives surface, background, and outline tones
    val neutralScheme = rememberDynamicColorScheme(
        seedColor = neutralSeed,
        isDark = darkTheme,
        isAmoled = false,
        style = PaletteStyle.Neutral
    )

    // Merge: primary/secondary/tertiary from expressive; surfaces/neutral from neutral
    val colorScheme = expressiveScheme.copy(
        background = neutralScheme.background,
        onBackground = neutralScheme.onBackground,
        surface = neutralScheme.surface,
        onSurface = neutralScheme.onSurface,
        surfaceVariant = neutralScheme.surfaceVariant,
        onSurfaceVariant = neutralScheme.onSurfaceVariant,
        surfaceDim = neutralScheme.surfaceDim,
        surfaceBright = neutralScheme.surfaceBright,
        surfaceContainerLowest = neutralScheme.surfaceContainerLowest,
        surfaceContainerLow = neutralScheme.surfaceContainerLow,
        surfaceContainer = neutralScheme.surfaceContainer,
        surfaceContainerHigh = neutralScheme.surfaceContainerHigh,
        surfaceContainerHighest = neutralScheme.surfaceContainerHighest,
        outline = neutralScheme.outline,
        outlineVariant = neutralScheme.outlineVariant,
        scrim = neutralScheme.scrim,
        inverseSurface = neutralScheme.inverseSurface,
        inverseOnSurface = neutralScheme.inverseOnSurface,
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        motionScheme = MotionScheme.expressive(),
    )
}

