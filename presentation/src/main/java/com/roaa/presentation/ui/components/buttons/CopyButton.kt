package com.roaa.presentation.ui.components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roaa.presentation.R

private const val COPY_ANIMATION_DURATION_MS = 200
private val IconLabelSpacing = 4.dp

@Composable
fun CopyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCopied: Boolean = false
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        AnimatedContent(
            targetState = isCopied,
            transitionSpec = {
                fadeIn(tween(COPY_ANIMATION_DURATION_MS)) togetherWith
                        fadeOut(tween(COPY_ANIMATION_DURATION_MS))
            },
            label = "CopyButtonContent"
        ) { copied ->
            CopyButtonContent(isCopied = copied)
        }
    }
}

@Composable
private fun CopyButtonContent(isCopied: Boolean) {
    val iconRes = if (isCopied) R.drawable.copied_check_icon else R.drawable.copy_icon
    val labelRes = if (isCopied) R.string.copy_button_copied else R.string.copy_button_copy
    val contentColor = MaterialTheme.colorScheme.onPrimary

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor)
        )
        Spacer(Modifier.width(IconLabelSpacing))
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.titleMedium,
            color = contentColor
        )
    }
}

@Preview
@Composable
private fun CopyButtonPreview() {
    CopyButton(onClick = {})
}

@Preview
@Composable
private fun CopyButtonCopiedPreview() {
    CopyButton(onClick = {}, isCopied = true)
}