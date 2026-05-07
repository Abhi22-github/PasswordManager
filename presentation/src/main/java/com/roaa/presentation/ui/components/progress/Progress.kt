package com.roaa.presentation.ui.components.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roaa.presentation.utils.PasswordStrength
import com.roaa.presentation.utils.PasswordStrengthObject

@Composable
fun SegmentedStrengthMeter(
    passwordStrengthObject: PasswordStrengthObject,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {

    val filledSegments = when (passwordStrengthObject.passwordStrength) {
        PasswordStrength.NONE -> 0
        PasswordStrength.POOR -> 1
        PasswordStrength.AVERAGE -> 2
        PasswordStrength.GOOD -> 3
        PasswordStrength.STRONG -> 4
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            val segmentColor by animateColorAsState(
                targetValue = if (index < filledSegments) passwordStrengthObject.passwordColor
                else MaterialTheme.colorScheme.surfaceContainerHigh,
                animationSpec = tween(300, delayMillis = index * 50),
                label = "segment_$index"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(segmentColor)
            )
        }

        AnimatedVisibility(visible = passwordStrengthObject.passwordStrength != PasswordStrength.NONE && showLabel) {
            Text(
                text = passwordStrengthObject.passwordStrength.label,
                style = MaterialTheme.typography.labelMedium,
                color = passwordStrengthObject.passwordColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}