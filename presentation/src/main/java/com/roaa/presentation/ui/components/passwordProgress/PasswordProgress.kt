package com.roaa.presentation.ui.components.passwordProgress

import android.graphics.Paint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roaa.presentation.ui.components.cards.StrengthBadge
import com.roaa.presentation.ui.theme.*
import com.roaa.presentation.utils.PasswordStrengthObject

private const val CONTENT_ANIM_DURATION_MS = 200
private const val COPIED_FEEDBACK_DURATION_MS = 2_000L

@Composable
fun RowScope.PasswordStrengthIndicator(
    passwordObject: PasswordStrengthObject, passwordText: String, modifier: Modifier = Modifier,
) {
    val percentWithNewSpentAnimated = animateFloatAsState(
        label = "percentWithNewSpentAnimated",
        targetValue = passwordObject.passwordScore,
        animationSpec = TweenSpec(300),
    ).value

    val harmonizedColor = harmonize(
        combineColors(
            listOf(
                colorBad,
                colorNotGood,
                colorGood,
            ),
            percentWithNewSpentAnimated.coerceIn(0f, 1f),
        ),
        colorEditor
    ).toPalette()

    Card(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = harmonizedColor.surface,
            contentColor = harmonizedColor.onContainer.copy(0.4f),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            BackgroundProgress(harmonizedColor = harmonizedColor, passwordObject.passwordScore)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp, horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = "Your password strength is ${passwordObject.passwordStrength.label}",
                    style = MaterialTheme.typography.titleSmall,
                    color = harmonizedColor.main,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

            }
        }
    }
}

fun ContentDrawScope.drawWithLayer(block: ContentDrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}

fun Modifier.drawWithLayer(block: ContentDrawScope.() -> Unit) = this.then(
    Modifier.drawWithContent {
        drawWithLayer {
            block()
        }
    }
)

