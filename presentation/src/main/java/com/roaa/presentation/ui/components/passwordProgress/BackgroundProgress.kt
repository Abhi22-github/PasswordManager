package com.roaa.presentation.ui.components.passwordProgress

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.roaa.presentation.ui.theme.HarmonizedColorPalette
import com.roaa.presentation.utils.clamp
import kotlinx.coroutines.launch


@Composable
fun BackgroundProgress(
    harmonizedColor: HarmonizedColorPalette,
    percent: Float, modifier: Modifier = Modifier,
) {

    val percentWithNewSpentAnimated = animateFloatAsState(
        label = "percentWithNewSpentAnimated",
        targetValue = percent,
        animationSpec = TweenSpec(300),
    ).value

    val shift = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        fun anim() {
            coroutineScope.launch {
                shift.animateTo(
                    1f,
                    animationSpec = FloatTweenSpec(4000, 0, LinearEasing)
                )
                shift.snapTo(0f)
                anim()
            }
        }
        anim()
    }

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.background(
                color = harmonizedColor.container,
                shape = WavyShape(
                    period = 40.dp,
                    amplitude = percentWithNewSpentAnimated.clamp(0.96f, 1f) * 2.dp,
                    shift = shift.value,
                ),
            )
                .fillMaxHeight()
                .fillMaxWidth(percentWithNewSpentAnimated),
        )
    }
}


