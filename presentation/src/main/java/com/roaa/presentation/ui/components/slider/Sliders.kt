package com.roaa.presentation.ui.components.slider

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

private const val DEFAULT_SLIDER_MIN = 0f
private const val DEFAULT_SLIDER_MAX = 10f
private const val DEFAULT_SLIDER_STEPS = 9

@Composable
fun GenericSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = DEFAULT_SLIDER_MIN..DEFAULT_SLIDER_MAX,
    steps: Int = DEFAULT_SLIDER_STEPS,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        interactionSource = interactionSource,
//        colors = SliderDefaults.colors(
//            thumbColor = MaterialTheme.colorScheme.primary,
//            activeTrackColor = MaterialTheme.colorScheme.primary,
//            inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerLow,
//        ),
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                thumbTrackGapSize = 6.dp, // gap between thumb and track
                trackInsideCornerSize = 4.dp,
                modifier = Modifier.height(25.dp)
            )
        },
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = remember { MutableInteractionSource() },
                thumbSize = DpSize(5.dp, 35.dp) // wide pill thumb
            )
        }
    )
}

@Preview
@Composable
private fun GenericSliderPreview() {
    GenericSlider(
        value = 5f,
        onValueChange = {}
    )
}