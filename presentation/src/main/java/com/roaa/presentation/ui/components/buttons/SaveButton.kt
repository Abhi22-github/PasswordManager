package com.roaa.presentation.ui.components.buttons

import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roaa.presentation.R

private val SaveButtonHeight = 48.dp
private const val DisabledContainerAlpha = 0.4f

@Composable
fun SaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = stringResource(R.string.save_button_label)
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.height(SaveButtonHeight),
        enabled = enabled,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = DisabledContainerAlpha),
            disabledContentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
private fun SaveButtonEnabledPreview() {
    SaveButton(onClick = {})
}

@Preview
@Composable
private fun SaveButtonDisabledPreview() {
    SaveButton(onClick = {}, enabled = false)
}