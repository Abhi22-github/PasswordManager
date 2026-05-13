package com.roaa.presentation.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roaa.presentation.R
import com.roaa.presentation.ui.components.cards.*
import com.roaa.presentation.ui.components.slider.GenericSlider
import com.roaa.presentation.ui.components.toggles.ToggleButton
import com.roaa.presentation.ui.theme.ToolbarBottomGap
import com.roaa.presentation.utils.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

private val SliderHorizontalPadding = 6.dp
private val SectionTopSpacing = 24.dp
private val SectionBottomSpacing = 16.dp
private const val GENERATE_DEBOUNCE_MS = 50L

private const val MIN_PASSWORD_LENGTH = 8
private const val MAX_PASSWORD_LENGTH = 32
private const val DEFAULT_PASSWORD_LENGTH = 12
private val textValueBackgroundRadius = 12.dp

/**
 * User-facing knobs for the password generator. Held as one snapshot so the
 * generation effect has a single dependency to observe.
 */
private data class GeneratorOptions(
    val length: Int = DEFAULT_PASSWORD_LENGTH,
    val includeDigits: Boolean = false,
    val includeSpecialChars: Boolean = false,
    val includeCapitalChars: Boolean = false
)

@OptIn(FlowPreview::class)
@Composable
fun PasswordGenerateScreen(
    onNavigateToAddPassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val errorMessage = stringResource(R.string.generator_error)
    val copyPassword = rememberPasswordClipboard()

    var options by remember { mutableStateOf(GeneratorOptions()) }
    var passwordText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        snapshotFlow { options }
            .distinctUntilChanged()
            .debounce(GENERATE_DEBOUNCE_MS)
            .collect { current ->
                passwordText = runCatching {
                    generatePassword(
                        current.length,
                        current.includeDigits,
                        current.includeSpecialChars,
                        current.includeCapitalChars
                    )
                }.getOrElse {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    ""
                }
            }
    }

    PasswordGenerateScreenContent(
        options = options,
        onOptionsChange = { options = it },
        passwordText = passwordText,
        onRegenerate = {
            passwordText = runCatching {
                generatePassword(
                    options.length,
                    options.includeDigits,
                    options.includeSpecialChars,
                    options.includeCapitalChars
                )
            }.getOrElse {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                ""
            }
        },
        onSave = { onNavigateToAddPassword(passwordText) },
        onCopy = { copyPassword(passwordText) },
        modifier = modifier
    )
}

@Composable
private fun PasswordGenerateScreenContent(
    options: GeneratorOptions,
    onOptionsChange: (GeneratorOptions) -> Unit,
    passwordText: String,
    onRegenerate: () -> Unit,
    onSave: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader()

        Spacer(Modifier.height(SectionTopSpacing))

        AnimatedVisibility(
            visible = passwordText.isNotEmpty(),
            enter = fadeIn(tween(1000)) + expandVertically()
        ) {
            PasswordGeneratorCard(
                passwordText = passwordText,
                onNewClick = onRegenerate,
                onSaveClick = onSave,
                onCopyClick = onCopy
            )
        }

        Spacer(Modifier.height(SectionTopSpacing))

        CardBackgroundStandard() {
            CardHeading(cardTitle = stringResource(R.string.generator_length))
            IntSlider(
                label = stringResource(R.string.generator_character),
                value = options.length,
                valueRange = MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH,
                onValueChange = { onOptionsChange(options.copy(length = it)) },
                modifier = Modifier.padding(horizontal = SliderHorizontalPadding)
            )
        }
        Spacer(Modifier.height(SectionTopSpacing))

        CardBackgroundStandard() {
            CardHeading(cardTitle = stringResource(R.string.generator_include))
            IncludeContentRow(
                rowTitle = stringResource(R.string.generator_uppercase),
                toggleButtonValue = options.includeCapitalChars,
                toggleButtonValueChanged = {
                    onOptionsChange(options.copy(includeCapitalChars = it))
                }
            )
            IncludeContentRow(
                rowTitle = stringResource(R.string.generator_digits),
                toggleButtonValue = options.includeDigits,
                toggleButtonValueChanged = {
                    onOptionsChange(options.copy(includeDigits = it))
                }
            )
            IncludeContentRow(
                rowTitle = stringResource(R.string.generator_special_chars),
                toggleButtonValue = options.includeSpecialChars,
                toggleButtonValueChanged = {
                    onOptionsChange(options.copy(includeSpecialChars = it))
                }
            )

        }

        Spacer(Modifier.height(ToolbarBottomGap))
    }
}

@Composable
private fun ScreenHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.generator_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.generator_subtitle),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun IntSlider(
    label: String,
    value: Int,
    valueRange: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(textValueBackgroundRadius)
                )
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
        GenericSlider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt().coerceIn(valueRange)) },
            valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
            steps = (valueRange.last - valueRange.first - 1).coerceAtLeast(0)
        )
    }
}


@Composable
fun IncludeContentRow(
    modifier: Modifier = Modifier,
    toggleButtonValue: Boolean,
    toggleButtonValueChanged: (Boolean) -> Unit,
    rowTitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rowTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        ToggleButton(checkValue = toggleButtonValue, checkValueChanged = toggleButtonValueChanged)
    }
}


@Preview
@Composable
private fun PasswordGenerateScreenPreview() {
    PasswordGenerateScreenContent(
        options = GeneratorOptions(),
        onOptionsChange = {},
        passwordText = "Abc12!Xy9z",
        onRegenerate = {},
        onSave = {},
        onCopy = {}
    )
}