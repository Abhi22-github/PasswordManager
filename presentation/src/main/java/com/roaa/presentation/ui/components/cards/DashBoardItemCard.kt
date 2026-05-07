package com.roaa.presentation.ui.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.*
import com.roaa.domain.model.Credentials
import com.roaa.domain.model.ServiceType
import com.roaa.presentation.R
import com.roaa.presentation.ui.actions.DashBoardItemCardActions
import kotlinx.coroutines.delay


private val CardPadding = 12.dp
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
                .padding(CardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ServiceAvatar(
                image = credentialsItem.logoUrl,
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val request = ImageRequest.Builder(context)
        .data(image)
        .crossfade(true)
        .build()

    AsyncImage(
        modifier = modifier
            .size(AvatarSize)
            .clip(CircleShape),
        model = request,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
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
        Text(
            text = username,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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