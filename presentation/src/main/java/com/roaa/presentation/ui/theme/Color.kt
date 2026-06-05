package com.roaa.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb  // still used by harmonizeWithColor
import com.materialkolor.PaletteStyle
import com.materialkolor.blend.Blend
import com.materialkolor.dynamicColorScheme
import kotlin.math.ceil
import kotlin.math.floor

val AndroidGreen = Color(0xFF3DDC84)

val blackColor = Color(0xFF000000)
val blue = Color(0xFF1C73F9)

// Seed for primary / secondary / tertiary tones (vivid)
val expressiveSeed = Color(0xFF1C73F9)

// Seed for surface / background / neutral tones (desaturated)
val neutralSeed = Color(0xFFFFFFFF)

val orangeColor = Color(0xFFFFA40B)
val darkBlue = Color(0xFF5A86CC)
val blueDarkBackground = Color(0xFF78ADFF)
val blueFaintBackground = Color(0xFFEAF1FB)
val blueCardColorOnBackGround = Color(0xFFF2F6FC)
val safeColor = Color(0xFF58CA65)
val reusedColor = Color(0xFFC491E1)
val compromiseColor = Color(0xFF7689D7)
val weakColor = Color(0xFFE58D8E)
val statCardBackground = Color(0xFF1F294F)
val customTextColor = Color(0xFF4E4E4E)

val ColorScheme.warning: Color
    get() = Color(0xFFE65100) // Orange 900 - deeper amber/orange

val ColorScheme.onWarning: Color
    get() = Color.White

val ColorScheme.warningContainer: Color
    get() = Color(0xFFFFCC80) // Orange 200 - muted container

val ColorScheme.onWarningContainer: Color
    get() = Color(0xFF3E2723)

val poorColor = Color(0xFFE53935)
val averageColor = Color(0xFFFB8C00)
val goodColor = Color(0xFFFDD835)
val strongColor = Color(0xFF43A047)

val colorSeed = Color(0xFFCC4C08)
val colorSeedTry = Color(0xff1a73e8)
val colorGood = Color(0xFF40AC02)
val colorNotGood = Color(0xFFFABC20)
val colorBad = Color(0xFFC70909)
val colorMin = Color(0xFF185ED6)
val colorMax = Color(0xFFDD1414)
val greyBackground = Color(0xfff2f2f2)
val greyTextColor = Color(0xFF616161)
val successColor = Color(0xFF086024)
val failureColor = Color(0xFFEA4335)
val blueColor = Color(0xFF1A73E8)
val purpleColor = Color(0xFF6C3BAA)
val greenColor = Color(0xFF216C34)
val orange = Color(0xFFDF7D1A)
val infoColor = Color(0xFFFFC107)

val color1 = Color(0xFFCD6A65)
val color2 = Color(0xFFD06DA6)
val color3 = Color(0xFF358C76)
val color4 = Color(0xFF4D67B5)
val color5 = Color(0xFF6D4CE1)
val color6 = Color(0xFF3C411E)
val color7 = Color(0xFF9CA1AD)
val color8 = Color(0xFFF79066)


val onboardingColor1 = Color(0xFFE8DBC9)
val onboardingColor2 = Color(0xFFCCCCCC)
val onboardingColor3 = Color(0xFFACD8D3)
val onboardingColor4 = Color(0xFFF1CE9C)
val onboardingColor5 = Color(0xFFEFC8B0)
val onboardingColor6 = Color(0xFFE0E7F3)
val onboardingColor7 = Color(0xFFE8DBC9)
val onboardingColor8 = Color(0xFFC6EFDD)

@Composable
fun harmonize(designColor: Color, sourceColor: Color = MaterialTheme.colorScheme.primary): Color {
    return harmonizeWithColor(designColor, sourceColor)
}

fun harmonizeWithColor(designColor: Color, sourceColor: Color): Color {
    return Color(Blend.harmonize(designColor.toArgb(), sourceColor.toArgb()))
}

@Composable
fun Color.toPalette(darkTheme: Boolean = isSystemInDarkTheme()): HarmonizedColorPalette {
    return toPaletteWithTheme(this, darkTheme)
}

fun toPaletteWithTheme(color: Color, darkTheme: Boolean): HarmonizedColorPalette {
    val scheme = dynamicColorScheme(
        seedColor = color,
        isDark = darkTheme,
        isAmoled = false,
        style = PaletteStyle.TonalSpot
    )
    return HarmonizedColorPalette(
        main = scheme.primary,
        onMain = scheme.onPrimary,
        container = scheme.primaryContainer,
        onContainer = scheme.onPrimaryContainer,
        surface = scheme.surface,
        onSurface = scheme.onSurface,
        surfaceVariant = scheme.surfaceVariant,
        onSurfaceVariant = scheme.onSurfaceVariant
    )
}

data class HarmonizedColorPalette(
    val main: Color,
    val onMain: Color,
    val container: Color,
    val onContainer: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
)

fun combineColors(colorA: Color, colorB: Color, angle: Float = 0.5F): Color {
    val colorAPart = (1F - angle) * 2
    val colorBPart = angle * 2

    return Color(
        red = (colorA.red * colorAPart + colorB.red * colorBPart) / 2,
        green = (colorA.green * colorAPart + colorB.green * colorBPart) / 2,
        blue = (colorA.blue * colorAPart + colorB.blue * colorBPart) / 2,
    )
}

fun combineColors(colors: List<Color>, angle: Float = 0.5F): Color {
    val approximateIndex = (colors.size - 1) * angle
    val colorA = colors[floor(approximateIndex).toInt()]
    val colorB = colors[ceil(approximateIndex).toInt()]

    return combineColors(colorA, colorB, approximateIndex - floor(approximateIndex))
}

val colorEditor
    @Composable
    @ReadOnlyComposable
    get() = combineColors(
        MaterialTheme.colorScheme.surfaceContainerLowest,
        MaterialTheme.colorScheme.surfaceVariant,
        0.90F,
    )


fun Color.darken(factor: Float = 0.4f): Color {
    val red = (red * (1 - factor)).coerceIn(0f, 1f)
    val green = (green * (1 - factor)).coerceIn(0f, 1f)
    val blue = (blue * (1 - factor)).coerceIn(0f, 1f)
    return Color(red, green, blue, alpha)
}