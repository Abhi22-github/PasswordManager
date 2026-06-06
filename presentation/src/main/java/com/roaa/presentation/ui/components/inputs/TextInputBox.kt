package com.roaa.presentation.ui.components.inputs

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val PlaceholderAlpha = 0.4f

private val InputCornerRadius = 20.dp
private val LabelToInputSpacing = 4.dp

@Composable
fun TextInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = if (singleLine) 1 else 5,
    maxLines: Int = if (singleLine) 1 else 5,
    isLoading: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    shape: Shape = RoundedCornerShape(InputCornerRadius),
    isPasswordTextField: Boolean = false,
    isDisable: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    BasicTextField(
        enabled = !isDisable,
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.titleMedium.copy(
            color = if (isDisable) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = singleLine,
        visualTransformation = if (passwordVisible || !isPasswordTextField) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(PlaceholderAlpha)
                        )
                    }
                    innerTextField()
                }

                AnimatedVisibility(isPasswordTextField) {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password"
                            else "Show password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                AnimatedVisibility(isLoading, enter = fadeIn(), exit = fadeOut()) {
                    ContainedLoadingIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        minLines = minLines,
        maxLines = maxLines,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LabeledTextInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isLoading: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLines: Int = 1,
    minLines: Int = 1,
    shape: Shape = RoundedCornerShape(InputCornerRadius),
    isPasswordTextField: Boolean = false,
    isDisable: Boolean = false
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (isDisable) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
            FieldLabel(text = label)
            Spacer(Modifier.height(LabelToInputSpacing))
            TextInputBox(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                modifier = Modifier,
                singleLine = singleLine,
                minLines = minLines,
                maxLines = maxLines,
                keyboardType = keyboardType,
                shape = shape,
                isLoading = isLoading,
                isPasswordTextField = isPasswordTextField,
                isDisable = isDisable,
            )
        }
    }
}

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(InputCornerRadius),
    shouldShowCopyButton: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp).weight(0.8f)) {
                FieldLabel(text = label)
                Spacer(Modifier.height(LabelToInputSpacing))
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (shouldShowCopyButton) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {},
                    shape = MaterialShapes.Square.toShape(),
                    modifier = Modifier.aspectRatio(1f).weight(0.2f)
                ) {
                    Icon(
                        painter = painterResource(com.roaa.presentation.R.drawable.copy_icon),
                        contentDescription = null
                    )
                }
            }
        }
    }
}


@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    )
}

@Preview
@Composable
private fun LabeledTextInputFieldPreviewForEmail() {
    LabeledTextInputField(
        value = "email",
        onValueChange = {},
        placeholder = "Email",
        label = "Email",
        modifier = Modifier,
        singleLine = true,
        isLoading = false,
    )
}

@Preview
@Composable
private fun LabeledTextInputFieldPreviewForPassword() {
    LabeledTextInputField(
        value = "Password",
        onValueChange = {},
        placeholder = "Password",
        label = "Email",
        modifier = Modifier,
        singleLine = true,
        isLoading = false,
        isPasswordTextField = true
    )
}