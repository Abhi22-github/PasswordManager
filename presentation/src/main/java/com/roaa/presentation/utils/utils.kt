package com.roaa.presentation.utils

import android.content.ClipData
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.roaa.domain.model.Credentials
import com.roaa.presentation.ui.theme.blackColor
import java.security.SecureRandom
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun colorizeText(text: String, colorScheme: ColorScheme): AnnotatedString {
    return buildAnnotatedString {
        text.forEach { char ->
            when {
                char.isLetter() && char.isUpperCase() -> withStyle(SpanStyle(color = colorScheme.onPrimaryContainer)) {
                    append(
                        char
                    )
                }

                char.isLetter() && char.isLowerCase() -> withStyle(SpanStyle(color = colorScheme.onPrimaryContainer)) {
                    append(
                        char
                    )
                }

                char.isDigit() -> withStyle(SpanStyle(color = colorScheme.secondary)) { append(char) }
                else -> withStyle(SpanStyle(color = colorScheme.tertiary)) { append(char) } // symbols etc.
            }
        }
    }
}

fun generateColorizedText(text: String, color: Color = blackColor): AnnotatedString {
    return buildAnnotatedString {
        text.forEach { char ->
            when {
                char.isLetter() && char.isUpperCase() -> withStyle(SpanStyle(color = blackColor)) {
                    append(
                        char
                    )
                }

                char.isLetter() && char.isLowerCase() -> withStyle(SpanStyle(color = blackColor)) {
                    append(
                        char
                    )
                }

                char.isDigit() -> withStyle(SpanStyle(color = color)) { append(char) }
                else -> withStyle(SpanStyle(color = blackColor)) { append(char) } // symbols etc.
            }
        }
    }
}

/**
 * Generates a random password using the enabled character categories.
 *
 * Lowercase letters are always included. For each enabled category, the password
 * is guaranteed to contain at least one character from it. Remaining slots are
 * filled randomly from the combined pool of enabled categories, then shuffled.
 *
 * @param length              total password length
 * @param includeDigits       include digits (0–9)
 * @param includeSpecialChars include special characters
 * @param includeUppercase    include uppercase letters (A–Z)
 * @return generated password
 * @throws IllegalArgumentException if length is too small for the enabled categories
 */
fun generatePassword(
    length: Int,
    includeDigits: Boolean,
    includeSpecialChars: Boolean,
    includeUppercase: Boolean
): String {
    require(length > 0) { "Length must be positive" }

    val digitPool = "0123456789"
    val specialPool = "!@#$%^&*()-_=+[]{};:,.<>?/"
    val uppercasePool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lowercasePool = "abcdefghijklmnopqrstuvwxyz"

    val random = SecureRandom()

    // Collect enabled pools — lowercase is always included as the base
    val enabledPools = buildList {
        add(lowercasePool)
        if (includeDigits) add(digitPool)
        if (includeSpecialChars) add(specialPool)
        if (includeUppercase) add(uppercasePool)
    }

    require(length >= enabledPools.size) {
        "Length ($length) must be at least ${enabledPools.size} to include one of each enabled category"
    }

    val chars = mutableListOf<Char>()

    // 1. Guarantee one character from each enabled pool
    enabledPools.forEach { pool -> chars.add(pool.random(random)) }

    // 2. Fill remaining slots from the combined pool
    val combinedPool = enabledPools.joinToString("")
    repeat(length - chars.size) {
        chars.add(combinedPool.random(random))
    }

    // 3. Shuffle so guaranteed chars aren't at the front
    chars.shuffle(random)

    return chars.joinToString("")
}

/** Helper: pick a random char from a String using a given Random. */
private fun String.random(random: SecureRandom): Char =
    this[random.nextInt(this.length)]




enum class BottomAppBarState {
    DashboardScreen,
    HealthScreen,
    PasswordGeneratorScreen,

}

@Composable
fun rememberPasswordClipboard(): (String) -> Unit {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    return remember {
        { password ->
            scope.launch {
                val clipData = ClipData.newPlainText("password", password)
                clipboard.setClipEntry(ClipEntry(clipData))

                delay(30_000)
                val current = clipboard.getClipEntry()?.clipData?.getItemAt(0)?.text?.toString()
                if (current == password) {
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("", "")))
                }
            }
        }
    }
}

data class PasswordInfoUiState(
    val credentials: Credentials? = null,
    val isLoading: Boolean = false,
    val notFound: Boolean = false,
    val logoImageUrl: String? = null,
)