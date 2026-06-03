package com.roaa.presentation.ui.components.toggles

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ToggleButton(
    modifier: Modifier = Modifier,
    checkValue: Boolean,
    checkValueChanged: (Boolean) -> Unit,
    color: Color,
) {


    Switch(
        checked = checkValue,
        onCheckedChange = {
            checkValueChanged(it)
        },
        colors = SwitchDefaults.colors()
    )
}

@Preview
@Composable
private fun ToggleButtonPreview() {
    ToggleButton(
        checkValue = true,
        checkValueChanged = {},
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview
@Composable
private fun TogglealseButtonPreview() {
    ToggleButton(
        checkValue = false,
        checkValueChanged = {},
        color = MaterialTheme.colorScheme.primary
    )
}