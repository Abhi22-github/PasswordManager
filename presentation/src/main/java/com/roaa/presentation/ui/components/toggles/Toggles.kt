package com.roaa.presentation.ui.components.toggles

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ToggleButton(
    modifier: Modifier = Modifier,
    checkValue: Boolean,
    checkValueChanged: (Boolean) -> Unit
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
        checkValue = false,
        checkValueChanged = {}
    )
}