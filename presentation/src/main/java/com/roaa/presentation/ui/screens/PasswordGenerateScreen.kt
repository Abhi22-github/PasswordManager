package com.roaa.presentation.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import com.roaa.presentation.R
import com.roaa.presentation.ui.components.appBar.DashBoardTopAppBar
import com.roaa.presentation.ui.components.cards.*
import com.roaa.presentation.ui.components.slider.GenericSlider
import com.roaa.presentation.ui.components.toggles.ToggleButton
import com.roaa.presentation.ui.theme.*
import com.roaa.presentation.utils.*
import kotlinx.coroutines.*
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

@Suppress("EffectKeys")
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
    val uppercaseColor = color1.toPalette()
    val symbolColor = color5.toPalette()
    val digitsColor = color2.toPalette()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DashBoardTopAppBar()

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

        Spacer(Modifier.height(8.dp))

        CardBackgroundStandard() {
            //  CardHeading(cardTitle = stringResource(R.string.generator_length))
            IntSlider(
                label = stringResource(R.string.generator_length),
                message = stringResource(R.string.generator_length_message),
                value = options.length,
                valueRange = MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH,
                onValueChange = { onOptionsChange(options.copy(length = it)) },
                modifier = Modifier.padding(horizontal = SliderHorizontalPadding)
            )
        }
        Spacer(Modifier.height(8.dp))

        CardBackgroundStandard() {
            //  CardHeading(cardTitle = stringResource(R.string.generator_include))
            IncludeContentRow(
                rowTitle = stringResource(R.string.generator_uppercase),
                rowDescription = stringResource(R.string.generator_uppercase_description),
                toggleButtonValue = options.includeCapitalChars,
                toggleButtonValueChanged = {
                    onOptionsChange(options.copy(includeCapitalChars = it))
                },
                containerColor = uppercaseColor.container,
                contentColor = uppercaseColor.onContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            IncludeContentRow(
                rowTitle = stringResource(R.string.generator_digits),
                rowDescription = stringResource(R.string.generator_digits_description),
                toggleButtonValue = options.includeDigits,
                toggleButtonValueChanged = {
                    onOptionsChange(options.copy(includeDigits = it))
                },
                containerColor = digitsColor.container,
                contentColor = digitsColor.onContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            IncludeContentRow(
                rowTitle = stringResource(R.string.generator_symbol),
                rowDescription = stringResource(R.string.generator_symbol_description),
                toggleButtonValue = options.includeSpecialChars,
                toggleButtonValueChanged = {
                    onOptionsChange(options.copy(includeSpecialChars = it))
                },
                containerColor = symbolColor.container,
                contentColor = symbolColor.onContainer
            )

        }

        Spacer(Modifier.height(ToolbarBottomGap))
    }
}

@Composable
private fun IntSlider(
    label: String,
    message: String,
    value: Int,
    valueRange: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val squareShape = remember { MaterialShapes.Square.normalized() }
    val cookieShape = remember { MaterialShapes.Cookie9Sided.normalized() }
    var isClicked by remember { mutableStateOf(false) }
    val morph = remember { Morph( cookieShape,squareShape) }
    val animatedProgress = animateFloatAsState(
        targetValue = if (isClicked) 1f else 0f,
        label = "progress",
        animationSpec = spring(dampingRatio = 0.2f, stiffness = Spring.StiffnessMedium)
    )
    LaunchedEffect(isClicked) {
        if (isClicked) {
            delay(1000)
            isClicked = false
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.8f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .weight(0.2f)
                    .aspectRatio(1f)
                    .clip(MorphPolygonShape(morph, animatedProgress.value))
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        GenericSlider(
            value = value.toFloat(),
            onValueChange = {
                onValueChange(it.toInt().coerceIn(valueRange))
                isClicked = true
            },
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
    rowTitle: String,
    rowDescription: String,
    contentColor: Color,
    containerColor: Color,
) {
    val squareShape = remember { MaterialShapes.Square.normalized() }
    val cookieShape = remember { MaterialShapes.Cookie9Sided.normalized() }
    val morph = remember { Morph(squareShape, cookieShape) }
    val animatedProgress = animateFloatAsState(
        targetValue = if (toggleButtonValue) 1f else 0f,
        label = "progress",
        animationSpec = spring(dampingRatio = 0.2f, stiffness = Spring.StiffnessMedium)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(MorphPolygonShape(morph, animatedProgress.value))
                .background(
                    color = containerColor,
                )
                .size(48.dp)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (rowTitle) {
                    stringResource(R.string.generator_uppercase) -> {
                        "A"
                    }

                    stringResource(R.string.generator_digits) -> {
                        "1"
                    }

                    stringResource(R.string.generator_symbol) -> {
                        "#"
                    }

                    else -> {
                        "~"
                    }
                },
                style = MaterialTheme.typography.titleLarge,
                color = contentColor
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = rowTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = rowDescription,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.72f)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        ToggleButton(
            checkValue = toggleButtonValue,
            checkValueChanged = toggleButtonValueChanged,
            color = contentColor
        )
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