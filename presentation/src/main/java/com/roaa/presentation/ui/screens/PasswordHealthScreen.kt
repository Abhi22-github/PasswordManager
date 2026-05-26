package com.roaa.presentation.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roaa.presentation.ui.components.appBar.DashBoardTopAppBar
import com.roaa.presentation.ui.theme.*
import com.roaa.presentation.utils.models.PasswordStats
import com.roaa.presentation.viewModels.PasswordStatViewModel

@Composable
fun PasswordHealthScreen(
    modifier: Modifier = Modifier, onBackClick: () -> Unit,
    passwordStatViewModel: PasswordStatViewModel = hiltViewModel()
) {
    val passwordStat by passwordStatViewModel.passwordStat.collectAsStateWithLifecycle()

    PasswordHealthScreenContent(
        modifier = modifier,
        passwordStats = passwordStat
    )
}

@Composable
fun PasswordHealthScreenContent(modifier: Modifier = Modifier, passwordStats: PasswordStats) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VerticalSpacingInConnectedCards)
    ) {
        DashBoardTopAppBar()
        Spacer(modifier = Modifier.height(EightDp))
        Image(
            painter = painterResource(com.roaa.presentation.R.drawable.illustration_password_health),
            contentDescription = "",
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
        )
        Spacer(modifier = Modifier.height(SixteenDp))
        PasswordHealthItemCard(
            cardShape = RoundedCornerShape(topEnd = GlobalCardRadius, topStart = GlobalCardRadius),
            iconBackgroundColor = MaterialTheme.colorScheme.errorContainer,
            iconColor = MaterialTheme.colorScheme.error,
            cardTitle = "${passwordStats.compromised} compromised password",
            cardSubTitle = stringResource(com.roaa.presentation.R.string.health_card_change_password_message)
        )
        PasswordHealthItemCard(
            cardShape = RoundedCornerShape(ZeroDp),
            iconBackgroundColor = MaterialTheme.colorScheme.warningContainer,
            iconColor = MaterialTheme.colorScheme.warning,
            cardTitle = "${passwordStats.reused} reused password",
            cardSubTitle = stringResource(com.roaa.presentation.R.string.health_card_create_unique_password)
        )
        PasswordHealthItemCard(
            cardShape = RoundedCornerShape(
                bottomEnd = GlobalCardRadius,
                bottomStart = GlobalCardRadius
            ),
            iconBackgroundColor = MaterialTheme.colorScheme.warningContainer,
            iconColor = MaterialTheme.colorScheme.warning,
            cardTitle = "${passwordStats.weak} weak password",
            cardSubTitle = stringResource(com.roaa.presentation.R.string.health_card_create_strong_password)
        )
    }
}

@Composable
fun PasswordHealthItemCard(
    cardShape: Shape,
    iconBackgroundColor: Color,
    iconColor: Color,
    cardTitle: String = "",
    cardSubTitle: String = "",
    modifier: Modifier = Modifier
) {
    GenericCardContainer(shape = cardShape) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(0.8f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(StandardCircle))
                        .background(iconBackgroundColor), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Info, contentDescription = "",
                        tint = iconColor
                    )
                }
                Spacer(modifier = Modifier.width(SixteenDp))
                Column() {
                    Text(
                        text = cardTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = cardSubTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                    )
                }
            }
            IconButton(
                onClick = {}, modifier = Modifier
                    .weight(0.1f)
                    .aspectRatio(1f)
            ) {
                Icon(
                    Icons.Filled.ArrowDropDown, contentDescription = ""
                )
            }
        }

    }
}

@Composable
fun GenericCardContainer(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(GlobalCardRadius),
    content: @Composable () -> Unit
) {
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(SixteenDp),
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