package com.roaa.presentation.ui.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roaa.presentation.R
import com.roaa.presentation.ui.components.progress.SegmentedStrengthMeter
import com.roaa.presentation.utils.*
import kotlinx.coroutines.delay

private val CardCornerRadius = 24.dp
private val CardPadding = 20.dp
private val PasswordTopSpacing = 16.dp
private val PasswordBottomSpacing = 12.dp
private val CardTitleBottomSpacing = 16.dp
private val PasswordToActionsSpacing = 16.dp
private val StrengthBadgePadding = 6.dp
private val StrengthBadgeEndPadding = 8.dp
private val StrengthBadgeCornerRadius = 12.dp
private val passwordTextCardInnerPadding = 16.dp
private const val CARD_HEADING_ALPHA = 0.7f
private const val CONTENT_ANIM_DURATION_MS = 200
private const val COPIED_FEEDBACK_DURATION_MS = 2_000L

private const val BADGE_BACKGROUND_ALPHA = 0.30f

@Composable
fun PasswordGeneratorCard(
    passwordText: String,
    onNewClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val passwordStrengthObject = remember(passwordText) { calculatePasswordStrength(passwordText) }

    CardBackgroundStandard() {
        PasswordLabelRow(passwordStrengthObject = passwordStrengthObject)
        Spacer(Modifier.height(PasswordTopSpacing))

        AnimatedPasswordText(
            modifier = Modifier,
            passwordText = passwordText
        )
        Spacer(Modifier.height(PasswordBottomSpacing))
        SegmentedStrengthMeter(
            passwordStrengthObject = passwordStrengthObject,
            modifier = Modifier,
            showLabel = false
        )

        Spacer(Modifier.height(PasswordToActionsSpacing))

        GeneratorActionsRow(
            onNewClick = onNewClick,
            onSaveClick = onSaveClick,
            onCopyClick = onCopyClick
        )
    }

}

@Composable
fun PasswordLabelRow(
    modifier: Modifier = Modifier,
    passwordStrengthObject: PasswordStrengthObject
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Your Password",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = CARD_HEADING_ALPHA)
        )
        StrengthBadge(
            passwordStrengthObject = passwordStrengthObject,
            modifier = Modifier
        )
    }
}

@Composable
fun CardHeading(
    modifier: Modifier = Modifier,
    cardTitle: String
) {
    Column() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cardTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = CARD_HEADING_ALPHA)
            )
        }
        Spacer(Modifier.height(CardTitleBottomSpacing))

    }
}

@Composable
private fun AnimatedPasswordText(
    passwordText: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        AnimatedContent(
            targetState = passwordText,
            transitionSpec = {
                fadeIn(tween(CONTENT_ANIM_DURATION_MS)) togetherWith
                        fadeOut(tween(CONTENT_ANIM_DURATION_MS))
            },
            label = "PasswordText",
            modifier = Modifier.padding(passwordTextCardInnerPadding)
        ) { current ->
            Text(
//                text = generateColorizedText(current),
                text = current,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun GeneratorActionsRow(
    onNewClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(COPIED_FEEDBACK_DURATION_MS)
            isCopied = false
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LabeledCircleAction(
            icon = R.drawable.refresh_icon,
            label = stringResource(R.string.password_generator_new),
            contentDescription = stringResource(R.string.password_generator_new),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            iconTint = MaterialTheme.colorScheme.onSurface,
            onClick = onNewClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
        )
        LabeledCircleAction(
            icon = R.drawable.bookmark_add_icon,
            label = stringResource(R.string.password_generator_save),
            contentDescription = stringResource(R.string.password_generator_save),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
            onClick = onSaveClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(0.dp)
        )

        AnimatedContent(
            targetState = isCopied,
            transitionSpec = {
                fadeIn(tween(CONTENT_ANIM_DURATION_MS)) togetherWith
                        fadeOut(tween(CONTENT_ANIM_DURATION_MS))
            },
            label = "CopyAction",
            modifier = Modifier.weight(1f)
        ) { copied ->
            LabeledCircleAction(
                icon = if (copied) R.drawable.copied_check_icon else R.drawable.copy_icon,
                label = stringResource(
                    if (copied) R.string.password_generator_copied
                    else R.string.password_generator_copy
                ),
                contentDescription = stringResource(R.string.password_generator_copy),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                iconTint = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    onCopyClick()
                    isCopied = true
                },
                shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
            )
        }
    }
}


@Composable
fun CardBackgroundStandard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardCornerRadius),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.outlinedCardElevation()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = CardPadding, vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}


@Composable
private fun LabeledCircleAction(
    @DrawableRes icon: Int,
    label: String,
    contentDescription: String?,
    containerColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = iconTint),
        shape = shape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = contentDescription,
                    tint = iconTint,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = iconTint
                )
            }
        }
    }
}

@Composable
private fun StrengthBadge(
    passwordStrengthObject: PasswordStrengthObject,
    modifier: Modifier = Modifier,
    strengthColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .background(
                color = passwordStrengthObject.passwordColor.copy(alpha = BADGE_BACKGROUND_ALPHA),
                shape = RoundedCornerShape(StrengthBadgeCornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = passwordStrengthObject.passwordStrength.label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(StrengthBadgePadding)
                .padding(horizontal = StrengthBadgeEndPadding),
            color = passwordStrengthObject.passwordColor
        )
    }
}


@Preview
@Composable
private fun CardBackgroundStandardPreview() {
    CardBackgroundStandard(
        content = {
            CardHeading(cardTitle = "Hello")
        }
    )
}

@Preview
@Composable
private fun PasswordGeneratorCardPreview() {
    PasswordGeneratorCard(
        passwordText = "Parth@Vyas@146uiux",
        onNewClick = {},
        onSaveClick = {},
        onCopyClick = {}
    )
}