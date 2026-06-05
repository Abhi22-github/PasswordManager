package com.roaa.presentation.utils

import androidx.compose.ui.graphics.Color
import com.roaa.presentation.ui.theme.averageColor
import com.roaa.presentation.ui.theme.goodColor
import com.roaa.presentation.ui.theme.poorColor
import com.roaa.presentation.ui.theme.strongColor

/**
 * Strength buckets — used for labels and accessibility announcements.
 */
enum class PasswordStrength(val label: String, val range: ClosedFloatingPointRange<Float>) {
    NONE("", 0f..0f),
    POOR("Poor", 0f..0.25f),
    AVERAGE("Average", 0.25f..0.5f),
    GOOD("Good", 0.5f..0.75f),
    STRONG("Strong", 0.75f..1f);

    companion object {
        fun fromScore(score: Float): PasswordStrength = when {
            score == 0f -> NONE
            score < 0.25f -> POOR
            score < 0.5f -> AVERAGE
            score < 0.75f -> GOOD
            else -> STRONG
        }
    }
}


/**
 * Theme-aware strength colors. Anchor points for interpolation.
 */
data class StrengthPalette(
    val poor: Color,
    val average: Color,
    val good: Color,
    val strong: Color,
) {
    companion object {
        fun light() = StrengthPalette(
            poor = poorColor,
            average = averageColor,
            good = goodColor,
            strong = strongColor,
        )
    }
}

/**
 * Smoothly interpolates between strength colors based on score (0f..1f).
 * This is what makes the bar feel alive — adjacent colors blend instead of snapping.
 */
private fun strengthColor(
    passwordStrength: PasswordStrength,
    palette: StrengthPalette = StrengthPalette.light()
): Color {
    return when (passwordStrength) {
        PasswordStrength.NONE -> {
            Color.Red
        }

        PasswordStrength.POOR -> {
            palette.poor
        }

        PasswordStrength.AVERAGE -> {
            palette.average
        }

        PasswordStrength.GOOD -> {
            palette.good
        }

        PasswordStrength.STRONG -> {
            palette.strong
        }
//        s == 0f -> {
//
//        }
//
//        s < 0.25f -> lerp(palette.poor, palette.average, s / 0.25f)
//        s < 0.5f -> lerp(palette.average, palette.good, (s - 0.25f) / 0.25f)
//        s < 0.75f -> lerp(palette.good, palette.veryGood, (s - 0.5f) / 0.25f)
//        else -> palette.veryGood
    }
}

data class PasswordStrengthObject(
    val passwordStrength: PasswordStrength,
    val passwordColor: Color,
    val passwordScore: Float
)

fun calculatePasswordStrength(password: String): PasswordStrengthObject {
    val passwordScore = calculatePasswordStrengthValue(password)
    val passwordStrength = calculatePasswordStrengthLabel(passwordScore)
    val passwordColor = strengthColor(passwordStrength)
    return PasswordStrengthObject(
        passwordStrength = passwordStrength,
        passwordColor = passwordColor,
        passwordScore = passwordScore
    )
}

/**
 * Returns password strength as string value.
 *
 * 0.00–0.25  Poor
 * 0.25–0.50  Average
 * 0.50–0.75  Good
 * 0.75–1.00  Very Good
 */
private fun calculatePasswordStrengthLabel(passwordStrengthValue: Float): PasswordStrength {
    return when {
        (passwordStrengthValue == 0f) -> PasswordStrength.NONE
        (passwordStrengthValue < 0.25) -> PasswordStrength.POOR
        (passwordStrengthValue < 0.50) -> PasswordStrength.AVERAGE
        (passwordStrengthValue < 0.75) -> PasswordStrength.GOOD
        else -> PasswordStrength.STRONG
    }
}

/**
 * Returns password strength as a 0f..1f score.
 *
 * 0.00–0.25  Poor
 * 0.25–0.50  Average
 * 0.50–0.75  Good
 * 0.75–1.00  Very Good
 */
fun calculatePasswordStrengthScore(password: String): Float = calculatePasswordStrengthValue(password)

private fun calculatePasswordStrengthValue(password: String): Float {
    if (password.isEmpty()) return 0f

    var score = 0

    // Length points (max 4)
    when {
        password.length >= 16 -> score += 4
        password.length >= 12 -> score += 3
        password.length >= 8 -> score += 2
        password.length >= 6 -> score += 1
    }

    // Character variety (max 4)
    if (password.any { it.isLowerCase() }) score += 1
    if (password.any { it.isUpperCase() }) score += 1
    if (password.any { it.isDigit() }) score += 1
    if (password.any { !it.isLetterOrDigit() }) score += 1

    // Normalize to 0f..1f (max possible = 8)
    return (score / 8f).coerceIn(0f, 1f)
}
