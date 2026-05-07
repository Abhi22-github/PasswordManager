package com.roaa.presentation.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roaa.presentation.R

private val OutlineButtonBorderWidth = 1.dp
private const val OutlineColorAlpha = 0.2f


@Composable
fun CircleWhiteButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    iconTint: Color = MaterialTheme.colorScheme.onSurface
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconTint,
        )
    }
}

@Composable
fun OutlineButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    shape: Shape,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    iconTint: Color = MaterialTheme.colorScheme.onSecondary,
    outlineColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = OutlineColorAlpha)
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        border = BorderStroke(OutlineButtonBorderWidth, outlineColor),
        contentPadding = PaddingValues(0.dp)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(iconTint)
        )
    }
}

@Preview
@Composable
private fun CircleWhiteButtonPreview() {
    CircleWhiteButton(
        onClick = {},
        icon = R.drawable.delete_icon
    )
}

@Preview
@Composable
private fun OutlineButtonPreview() {
    OutlineButton(
        onClick = {},
        icon = R.drawable.delete_icon,
        shape = RoundedCornerShape(3.dp)
    )
}