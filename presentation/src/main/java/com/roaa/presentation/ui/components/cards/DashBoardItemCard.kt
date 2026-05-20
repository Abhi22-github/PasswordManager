package com.roaa.presentation.ui.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil3.compose.AsyncImage
import coil3.request.*
import com.roaa.domain.model.*
import com.roaa.presentation.R
import com.roaa.presentation.ui.actions.DashBoardItemCardActions
import com.roaa.presentation.ui.theme.AndroidGreen
import com.roaa.presentation.ui.theme.CardInnerPadding
import kotlinx.coroutines.delay
import kotlin.math.*


private val AvatarSize = 36.dp
private val ActionButtonSize = 36.dp
private val ActionIconSize = 24.dp
private val AvatarToTextSpacing = 12.dp
private const val COPY_ANIMATION_DURATION_MS = 300

@Composable
fun DashBoardItemCard(
    shape: Shape,
    credentialsItem: Credentials,
    onAction: (DashBoardItemCardActions) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onAction(DashBoardItemCardActions.OnCardClicked(credentialsItem.id)) },
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CardInnerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ServiceAvatar(
                image = credentialsItem.logoUrl,
                serviceType = credentialsItem.serviceType,
                contentDescription = null
            )

            Spacer(Modifier.width(AvatarToTextSpacing))

            ServiceLabel(
                serviceName = credentialsItem.serviceName,
                username = credentialsItem.username,
                modifier = Modifier.weight(1f)
            )

            CardActions(
                onCopyClick = { onAction(DashBoardItemCardActions.OnCopyClicked(credentialsItem.password)) },
                onMoreClick = { onAction(DashBoardItemCardActions.OnMoreClicked(credentialsItem.id)) }
            )
        }
    }
}

@Composable
private fun ServiceAvatar(
    image: String?,
    contentDescription: String?,
    serviceType: ServiceType,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val request = ImageRequest.Builder(context)
        .data(image)
        .crossfade(true)
        .build()

    when (serviceType) {
        ServiceType.APP -> {
            WavyCircleBackground(
                modifier = modifier,
                size = AvatarSize
            ) {
                AsyncImage(
                    modifier = Modifier.padding(4.dp),
                    model = R.drawable.android_icon,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White)

                )
            }
        }

        ServiceType.WEBSITE -> {
            AsyncImage(
                modifier = modifier
                    .size(AvatarSize)
                    .clip(CircleShape),
                model = request,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                colorFilter = null
            )
        }
    }
}


@Composable
fun WavyCircleBackground(
    modifier: Modifier = Modifier,
    color: Color = AndroidGreen,
    waves: Int = 8,
    amplitude: Float = 0.08f, // 0.0 = perfect circle, 0.2 = very wavy
    size: Dp = 120.dp,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(size)
            .drawBehind {
                val centerX = this.size.width / 2f
                val centerY = this.size.height / 2f
                val radius = minOf(centerX, centerY)
                val ampPx = radius * amplitude

                val path = Path()
                val steps = 360
                for (i in 0..steps) {
                    val angle = (i * Math.PI * 2 / steps).toFloat()
                    // Sinusoidal wave around the circle
                    val wave = sin(angle * waves) * ampPx
                    val r = radius - ampPx + wave + ampPx
                    val x = centerX + cos(angle) * r
                    val y = centerY + sin(angle) * r

                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()

                drawPath(path = path, color = color)
            },
        content = { content() }
    )
}

@Composable
private fun ServiceLabel(
    serviceName: String,
    username: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = serviceName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if(false)
        Text(
            text = username,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CardActions(
    onCopyClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCopied by remember { mutableStateOf(false) }
    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(3000)
            isCopied = false
        }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        AnimatedContent(
            targetState = isCopied,
            transitionSpec = {
                fadeIn(tween(COPY_ANIMATION_DURATION_MS)) + scaleIn() togetherWith
                        fadeOut(tween(COPY_ANIMATION_DURATION_MS)) + scaleOut()
            },
            label = "CopyButtonContent"
        ) { copied ->
            CardActionButton(
                iconRes = if (copied) R.drawable.copied_check_icon else R.drawable.copy_icon,
                contentDescription = stringResource(R.string.dashboard_card_copy),
                onClick = {
                    onCopyClick()
                    isCopied = true
                }
            )
        }
        CardActionButton(
            iconRes = R.drawable.more_vert_icon,
            contentDescription = stringResource(R.string.dashboard_card_more),
            onClick = onMoreClick
        )
    }
}

@Composable
private fun CardActionButton(
    @DrawableRes iconRes: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(ActionButtonSize)
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(ActionIconSize),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer)
        )
    }
}

@Preview
@Composable
private fun DashBoardItemCardPreview() {
    DashBoardItemCard(
        credentialsItem =
            Credentials(
                id = "1",
                serviceName = "Gmail",
                username = "abhishek@example.com",
                password = "",
                domainName = "Gmail.com",
                logoUrl = "",
                serviceType = ServiceType.APP
            ),
        onAction = {},
        shape = RoundedCornerShape(1.dp)
    )
}