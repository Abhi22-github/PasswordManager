package com.roaa.presentation.ui.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.roaa.presentation.R
import com.roaa.presentation.ui.components.buttons.*
import com.roaa.presentation.utils.*
import kotlinx.coroutines.delay

private val CardCornerRadius = 32.dp
private val CardPadding = 20.dp
private val AvatarContainerSize = 108.dp
private val AvatarIconSize = 64.dp
private val ActionButtonSize = 48.dp
private val HeaderActionSpacing = 4.dp
private val BottomActionSpacing = 6.dp
private val SectionSpacingSmall = 16.dp
private val SectionSpacingLarge = 24.dp
private const val COPIED_FEEDBACK_DURATION_MS = 2_000L

@Composable
fun PasswordInfoCard(
    serviceName: String,
    username: String,
    password: String,
    serviceIcon: String?,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(CardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ServiceAvatar(image = serviceIcon)

            Spacer(Modifier.height(SectionSpacingSmall))

            ServiceHeaderRow(
                serviceName = serviceName,
                username = username,
                onEditClick = onEditClick,
                onShareClick = onShareClick
            )

            Spacer(Modifier.height(SectionSpacingSmall))

            Text(
                text = colorizeText(password, MaterialTheme.colorScheme),
                style = MaterialTheme.typography.headlineLarge,
                overflow = TextOverflow.Visible
            )

            Spacer(Modifier.height(SectionSpacingLarge))

            BottomActions(
                onDeleteClick = onDeleteClick,
                onCopyClick = onCopyPassword
            )
        }
    }
}

@Composable
fun ServiceAvatar(
    image: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val request = ImageRequest.Builder(context)
        .data(image)
        .crossfade(true)
        .build()
    Box(
        modifier = modifier.size(AvatarContainerSize),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier
                .size(AvatarContainerSize)
                .clip(CircleShape),
            model = request,
            contentDescription = "",
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun ServiceHeaderRow(
    serviceName: String,
    username: String,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = serviceName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = username,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        SecondaryCircleButton(
            icon = R.drawable.edit_icon,
            contentDescription = stringResource(R.string.password_info_edit),
            onClick = onEditClick
        )
        Spacer(Modifier.width(HeaderActionSpacing))
        SecondaryCircleButton(
            icon = R.drawable.share_icon,
            contentDescription = stringResource(R.string.password_info_share),
            onClick = onShareClick
        )
    }
}

@Composable
private fun BottomActions(
    onDeleteClick: () -> Unit,
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryCircleButton(
            icon = R.drawable.delete_icon,
            contentDescription = stringResource(R.string.password_info_delete),
            onClick = onDeleteClick
        )
        Spacer(Modifier.width(BottomActionSpacing))
        CopyButton(
            onClick = {
                onCopyClick()
                isCopied = true
            },
            modifier = Modifier
                .weight(1f)
                .height(ActionButtonSize),
            isCopied = isCopied
        )
    }
}

@Composable
private fun SecondaryCircleButton(
    @DrawableRes icon: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CircleWhiteButton(
        onClick = onClick,
        icon = icon,
        modifier = modifier.size(ActionButtonSize),
        contentDescription = contentDescription,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        iconTint = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Preview
@Composable
private fun PasswordInfoCardPreview() {
    val copyPassword = rememberPasswordClipboard()
    val password = "Parth@Vyas@146uiux"

    PasswordInfoCard(
        serviceName = "Facebook",
        username = "user.email@gmail.com",
        password = password,
        serviceIcon = "",
        onEditClick = {},
        onShareClick = {},
        onDeleteClick = {},
        onCopyPassword = { copyPassword(password) }
    )
}