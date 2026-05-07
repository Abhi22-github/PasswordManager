package com.roaa.presentation.ui.components.cards

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roaa.presentation.R
import androidx.compose.ui.res.stringResource

private val CardCornerRadius = 32.dp
private val CardPaddingHorizontal = 20.dp
private val CardPaddingTop = 20.dp
private val CardPaddingBottom = 72.dp

private val IndicatorSize = 128.dp
private val InnerRingSize = 164.dp
private val OuterRingSize = 189.dp
private val RingBorderWidth = 5.dp
private val WavyWavelength = 30.dp
private const val INDICATOR_STROKE_PX = 44f
private const val INDICATOR_TRACK_STROKE_PX = 28f
private const val INDICATOR_AMPLITUDE = 0f

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PasswordStrengthCard(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val percentText = stringResource(
        R.string.password_strength_percent,
        (clampedProgress * 100).toInt()
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = CardPaddingHorizontal,
                    end = CardPaddingHorizontal,
                    top = CardPaddingTop,
                    bottom = CardPaddingBottom
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StrengthHaloIndicator(
                progress = clampedProgress,
                centerLabel = percentText
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StrengthHaloIndicator(
    progress: Float,
    centerLabel: String,
    modifier: Modifier = Modifier
) {
    val ringColor = MaterialTheme.colorScheme.surfaceContainerLow

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DecorativeRing(size = OuterRingSize, color = ringColor)
        DecorativeRing(size = InnerRingSize, color = ringColor)

        CircularWavyProgressIndicator(
            modifier = Modifier.size(IndicatorSize),
            progress = { progress },
            stroke = Stroke(width = INDICATOR_STROKE_PX),
            trackStroke = Stroke(width = INDICATOR_TRACK_STROKE_PX),
            amplitude = { INDICATOR_AMPLITUDE },
            wavelength = WavyWavelength,
            color = MaterialTheme.colorScheme.primary,
            trackColor = ringColor
        )

        Text(
            text = centerLabel,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DecorativeRing(
    size: androidx.compose.ui.unit.Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .border(width = RingBorderWidth, color = color, shape = CircleShape)
    )
}

@Preview
@Composable
private fun PasswordStrengthCardPreview() {
    PasswordStrengthCard(progress = 0.77f)
}