package com.roaa.presentation.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.*
import com.airbnb.lottie.compose.*
import com.roaa.domain.model.*
import com.roaa.presentation.R
import com.roaa.presentation.ui.components.appBar.DashBoardTopAppBar
import com.roaa.presentation.ui.theme.*
import com.roaa.presentation.utils.models.PasswordStats
import com.roaa.presentation.viewModels.PasswordStatViewModel


@Composable
fun PasswordHealthScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onEditPassword: (String) -> Unit = {},
    passwordStatViewModel: PasswordStatViewModel = hiltViewModel()
) {
    val passwordStat by passwordStatViewModel.passwordStat.collectAsStateWithLifecycle()
    val weakPasswords by passwordStatViewModel.weakPasswords.collectAsStateWithLifecycle()
    val reusedPasswords by passwordStatViewModel.reusedPasswords.collectAsStateWithLifecycle()
    val compromisedPasswords by passwordStatViewModel.compromisedPasswords.collectAsStateWithLifecycle()

    PasswordHealthScreenContent(
        modifier = modifier,
        passwordStats = passwordStat,
        weakPasswords = weakPasswords,
        reusedPasswords = reusedPasswords,
        compromisedPasswords = compromisedPasswords,
        onEditPassword = onEditPassword
    )
}

@Composable
fun PasswordHealthScreenContent(
    modifier: Modifier = Modifier,
    passwordStats: PasswordStats,
    weakPasswords: List<Credentials> = emptyList(),
    reusedPasswords: List<Credentials> = emptyList(),
    compromisedPasswords: List<Credentials> = emptyList(),
    onEditPassword: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = ToolbarBottomGap),
        verticalArrangement = Arrangement.spacedBy(VerticalSpacingInConnectedCards)
    ) {
        DashBoardTopAppBar()
        Spacer(modifier = Modifier.height(EightDp))
//        Image(
//            painter = painterResource(R.drawable.illustration_password_health),
//            contentDescription = "",
//            modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
//        )
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PasswordStatsAnimation()
        }
        Spacer(modifier = Modifier.height(SixteenDp))
        Text(
            text = "Password checked for ${passwordStats.total} sites and apps",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.align(Alignment.CenterHorizontally)
        )

        val compromisedColor = failureColor.toPalette()
        val reusedColor = orangeColor.toPalette()
        val weakColor = orangeColor.toPalette()
        val healthyColor = strongColor.toPalette()

        // Compromised — list-based check since SQL groups encrypted bytes
        val isCompromisedSafe = compromisedPasswords.isEmpty()
        PasswordHealthItemCard(
            cardShape = RoundedCornerShape(topEnd = GlobalCardRadius, topStart = GlobalCardRadius),
            icon = if (isCompromisedSafe) R.drawable.check_circle_icon else R.drawable.info_circle_icon,
            iconBackgroundColor = if (isCompromisedSafe) healthyColor.container
            else MaterialTheme.colorScheme.errorContainer.copy(0.2f),
            iconColor = if (isCompromisedSafe) healthyColor.main else MaterialTheme.colorScheme.error,
            cardTitle = if (isCompromisedSafe) "All passwords are safe"
            else "${compromisedPasswords.size} compromised password",
            cardSubTitle = if (isCompromisedSafe) "No compromised passwords found"
            else stringResource(R.string.health_card_change_password_message),
            passwords = compromisedPasswords,
            onEditPassword = onEditPassword
        )

        // Reused — use decrypted list size; SQL count is unreliable with encrypted storage
        val isReusedSafe = reusedPasswords.isEmpty()
        PasswordHealthItemCard(
            cardShape = RoundedCornerShape(ZeroDp),
            icon = if (isReusedSafe) R.drawable.check_circle_icon else R.drawable.info_circle_icon,
            iconBackgroundColor = if (isReusedSafe) healthyColor.container
            else MaterialTheme.colorScheme.warningContainer.copy(0.2f),
            iconColor = if (isReusedSafe) healthyColor.main else MaterialTheme.colorScheme.warning,
            cardTitle = if (isReusedSafe) "All passwords are unique"
            else "${reusedPasswords.size} reused password",
            cardSubTitle = if (isReusedSafe) "No reused passwords found"
            else stringResource(R.string.health_card_create_unique_password),
            passwords = reusedPasswords,
            onEditPassword = onEditPassword
        )

        val isWeakSafe = weakPasswords.isEmpty()
        PasswordHealthItemCard(
            cardShape = RoundedCornerShape(
                bottomEnd = GlobalCardRadius,
                bottomStart = GlobalCardRadius
            ),
            icon = if (isWeakSafe) R.drawable.check_circle_icon else R.drawable.info_circle_icon,
            iconBackgroundColor = if (isWeakSafe) healthyColor.container
            else MaterialTheme.colorScheme.warningContainer.copy(0.2f),
            iconColor = if (isWeakSafe) healthyColor.main else MaterialTheme.colorScheme.warning,
            cardTitle = if (isWeakSafe) "All passwords are strong"
            else "${weakPasswords.size} weak password",
            cardSubTitle = if (isWeakSafe) "No weak passwords found"
            else stringResource(R.string.health_card_create_strong_password),
            passwords = weakPasswords,
            onEditPassword = onEditPassword
        )
    }
}

@Composable
fun PasswordStatsAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.password_stat_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // or a fixed count
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(200.dp)
    )
}

@Composable
fun PasswordHealthItemCard(
    cardShape: Shape,
    iconBackgroundColor: Color,
    iconColor: Color,
    icon: Int = R.drawable.info_circle_icon,
    cardTitle: String = "",
    cardSubTitle: String = "",
    passwords: List<Credentials> = emptyList(),
    onEditPassword: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "ArrowRotation"
    )
    val isExpandable = passwords.isNotEmpty()

    GenericCardContainer(
        shape = cardShape,
        onClick = { if (isExpandable) isExpanded = !isExpanded }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(StandardCircle))
                            .background(iconBackgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "",
                            tint = iconColor
                        )
                    }
                    Spacer(modifier = Modifier.width(SixteenDp))
                    Column {
                        Text(
                            text = cardTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = cardSubTitle,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                        )
                    }
                }
                if (isExpandable) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(arrowRotation),
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(300)) + fadeOut(tween(200))
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Spacer(Modifier.height(6.dp))
                    passwords.forEach { credentials ->
                        PasswordHealthListItem(
                            credentials = credentials,
                            onEditClick = { onEditPassword(credentials.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordHealthListItem(
    credentials: Credentials,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (credentials.serviceType) {
                ServiceType.APP -> Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(MaterialShapes.Cookie7Sided.toShape())
                        .background(
                            AndroidGreen,
                            MaterialShapes.Cookie7Sided.toShape()
                        )
                ) {
                    AsyncImage(
                        model = R.drawable.android_icon,
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }

                ServiceType.WEBSITE -> AsyncImage(
                    model = if (credentials.logoUrl.isNullOrEmpty()) {
                        R.drawable.website_icon
                    } else {
                        ImageRequest.Builder(context)
                            .data(credentials.logoUrl)
                            .crossfade(true)
                            .build()
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = credentials.serviceName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (credentials.username.isNotBlank()) {
                    Text(
                        text = credentials.username,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            TextButton(onClick = onEditClick) {
                Text(
                    text = "Change",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun GenericCardContainer(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(GlobalCardRadius),
    color: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = color),
        onClick = onClick ?: {},
        enabled = onClick != null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(SixteenDp)
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun PasswordHealthScreenContentPreview() {
    PasswordHealthScreenContent(passwordStats = PasswordStats(0, 0, 0, 0, 0))
}
