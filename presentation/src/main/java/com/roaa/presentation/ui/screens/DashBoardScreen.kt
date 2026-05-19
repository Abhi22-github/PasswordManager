package com.roaa.presentation.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roaa.domain.model.Credentials
import com.roaa.presentation.R
import com.roaa.presentation.ui.actions.*
import com.roaa.presentation.ui.components.appBar.DashBoardTopAppBar
import com.roaa.presentation.ui.components.cards.DashBoardItemCard
import com.roaa.presentation.ui.theme.*
import com.roaa.presentation.utils.models.PasswordStats
import com.roaa.presentation.viewModels.*

private val SectionSpacing = 12.dp

private val HeroTextSize = 52.sp
private val CardCornerRadius = 20.dp

@Composable
fun DashBoardScreen(
    onAction: (DashboardActions) -> Unit,
    modifier: Modifier = Modifier,
    passwordViewModel: PasswordViewModel = hiltViewModel(),
    dashboardViewModel: PasswordStatViewModel = hiltViewModel()

) {
    val passwords by passwordViewModel.allPasswords.collectAsStateWithLifecycle()
    val passwordStats by dashboardViewModel.passwordStat.collectAsStateWithLifecycle()


    DashBoardScreenContent(
        credentials = passwords,
        stats = passwordStats,
        onAction = onAction,
        modifier = modifier
    )
}

@Composable
private fun DashBoardScreenContent(
    credentials: List<Credentials>,
    stats: PasswordStats,
    onAction: (DashboardActions) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(VerticalSpacingInConnectedCards)
    ) {
        item(key = "top_app_bar") {
            DashBoardTopAppBar()
            Spacer(modifier = Modifier.height(EightDp))
        }

//        item(key = "hero") {
//            DashBoardHero()
//        }
//
//        item(key = "password_health_header") {
//            SectionHeader(text = stringResource(R.string.dashboard_section_password_health))
//        }

        item(key = "password_health_card") {
//            PasswordOverviewCard(
//                stats = stats,
//                onClick = { /* TODO: navigate to password health detail */ }
//            )
//            Spacer(Modifier.height(10.dp))
//            PasswordHealthScoreCard(
//                safe = 1,
//                weak = 4,
//                reused = 5,
//                compromised =4,
//                modifier = Modifier
//            )
//            Spacer(Modifier.height(10.dp))
//            PasswordHealthCardDonut(
//                safe = 1,
//                weak = 4,
//                reused = 5,
//                compromised =4,
//                modifier = Modifier
//            )
//            Spacer(Modifier.height(10.dp))
//            PasswordHealthCard(
//                safe = 1,
//                weak = 4,
//                reused = 5,
//                compromised = 4,
//                modifier = Modifier
//            )
        }

//        item(key = "domains_header") {
//            SectionHeader(
//                text = stringResource(R.string.dashboard_section_domains),
//                modifier = Modifier.padding(top = SectionToHeaderSpacing)
//            )
//        }

        itemsIndexed(
            items = credentials,
            key = { _, item -> item.id }
        ) { index, items ->
            val shape = when {
                credentials.size == 1 -> RoundedCornerShape(CardCornerRadius)
                index == 0 -> RoundedCornerShape(
                    topStart = CardCornerRadius,
                    topEnd = CardCornerRadius
                )

                index == credentials.lastIndex -> RoundedCornerShape(
                    bottomStart = CardCornerRadius,
                    bottomEnd = CardCornerRadius
                )

                else -> RectangleShape
            }
            DashBoardItemCard(
                modifier = Modifier.animateItem(),
                shape = shape,
                credentialsItem = items,
                onAction = { action -> handleItemCardAction(action, onAction) }
            )
        }

        item(key = "bottom_spacer") {
            Spacer(Modifier.height(ToolbarBottomGap))
        }
    }
}

@Composable
fun PasswordHealthCard(
    safe: Int,
    weak: Int,
    reused: Int,
    compromised: Int,
    modifier: Modifier = Modifier
) {
    val total = safe + weak + reused + compromised

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Passwords",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "$total",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Health percentage badge
                val healthPercent = if (total > 0) (safe * 100) / total else 0
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when {
                        healthPercent >= 80 -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                        healthPercent >= 50 -> Color(0xFFFB8C00).copy(alpha = 0.15f)
                        else -> Color(0xFFE53935).copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        "$healthPercent% safe",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            healthPercent >= 80 -> Color(0xFF2E7D32)
                            healthPercent >= 50 -> Color(0xFFEF6C00)
                            else -> Color(0xFFC62828)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Segmented horizontal bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                if (safe > 0) {
                    Box(
                        modifier = Modifier
                            .weight(safe.toFloat())
                            .fillMaxHeight()
                            .background(Color(0xFF4CAF50))
                    )
                }
                if (weak > 0) {
                    Box(
                        modifier = Modifier
                            .weight(weak.toFloat())
                            .fillMaxHeight()
                            .background(Color(0xFFE53935))
                    )
                }
                if (reused > 0) {
                    Box(
                        modifier = Modifier
                            .weight(reused.toFloat())
                            .fillMaxHeight()
                            .background(Color(0xFFFB8C00))
                    )
                }
                if (compromised > 0) {
                    Box(
                        modifier = Modifier
                            .weight(compromised.toFloat())
                            .fillMaxHeight()
                            .background(Color(0xFF9C27B0))
                    )
                }
            }

            // Legend
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LegendRow("Safe", safe, Color(0xFF4CAF50))
                LegendRow("Weak", weak, Color(0xFFE53935))
                LegendRow("Reused", reused, Color(0xFFFB8C00))
                LegendRow("Compromised", compromised, Color(0xFF9C27B0))
            }
        }
    }
}


@Composable
fun PasswordHealthCardDonut(
    safe: Int,
    weak: Int,
    reused: Int,
    compromised: Int,
    modifier: Modifier = Modifier
) {
    val total = (safe + weak + reused + compromised).coerceAtLeast(1)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Donut chart
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 16.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    var startAngle = -90f

                    listOf(
                        safe to Color(0xFF4CAF50),
                        weak to Color(0xFFE53935),
                        reused to Color(0xFFFB8C00),
                        compromised to Color(0xFF9C27B0)
                    ).forEach { (count, color) ->
                        if (count > 0) {
                            val sweep = (count.toFloat() / total) * 360f
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += sweep
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$total",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Stats list
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LegendRow("Safe", safe, Color(0xFF4CAF50))
                LegendRow("Weak", weak, Color(0xFFE53935))
                LegendRow("Reused", reused, Color(0xFFFB8C00))
                LegendRow("Compromised", compromised, Color(0xFF9C27B0))
            }
        }
    }
}

@Composable
private fun LegendRow(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            "$count",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PasswordHealthScoreCard(
    safe: Int,
    weak: Int,
    reused: Int,
    compromised: Int,
    modifier: Modifier = Modifier
) {
    val total = (safe + weak + reused + compromised).coerceAtLeast(1)
    val score = (safe * 100) / total

    val (scoreColor, scoreLabel) = when {
        score >= 80 -> Color(0xFF2E7D32) to "Excellent"
        score >= 60 -> Color(0xFF7CB342) to "Good"
        score >= 40 -> Color(0xFFFB8C00) to "Fair"
        else -> Color(0xFFE53935) to "Needs attention"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Security Score",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "$score",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )
                Text(
                    "/100",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                scoreLabel,
                style = MaterialTheme.typography.titleMedium,
                color = scoreColor,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Mini stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MiniStat("Safe", safe, Color(0xFF4CAF50))
                MiniStat("Weak", weak, Color(0xFFE53935))
                MiniStat("Reused", reused, Color(0xFFFB8C00))
                MiniStat("Risk", compromised, Color(0xFF9C27B0))
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "$count",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun StatCard(
    label: String,
    count: Int,
    color: Color,
    icon: ImageVector,
    modifier: Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DashBoardHero(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HeroLine(
            text = stringResource(R.string.dashboard_hero_keep),
            iconRes = R.drawable.lock_illustration,
            iconLeading = false
        )
        HeroLine(
            text = stringResource(R.string.dashboard_hero_your_life),
            iconRes = R.drawable.life_illustration,
            iconLeading = true
        )
        HeroLine(
            text = stringResource(R.string.dashboard_hero_safe),
            iconRes = R.drawable.security_shield_illustration,
            iconLeading = false
        )
        Spacer(Modifier.height(SectionSpacing))
    }
}

@Composable
private fun HeroLine(
    text: String,
    @DrawableRes iconRes: Int,
    iconLeading: Boolean,
    modifier: Modifier = Modifier
) {
    val textComposable: @Composable () -> Unit = {
        Text(
            text = text,
            fontSize = HeroTextSize,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
//    val iconComposable: @Composable () -> Unit = {
//        Image(
//            painter = painterResource(iconRes),
//            contentDescription = null,
//            modifier = Modifier.size(HeroIconSize)
//        )
//    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconLeading) {
            // iconComposable()
            textComposable()
        } else {
            textComposable()
            //iconComposable()
        }
    }
}

@Composable
private fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 2.dp
        )
    )
}

private fun handleItemCardAction(
    action: DashBoardItemCardActions,
    onScreenAction: (DashboardActions) -> Unit
) {
    when (action) {
        is DashBoardItemCardActions.OnCardClicked -> {
            onScreenAction(DashboardActions.OnCardClicked(action.passwordId))
        }

        is DashBoardItemCardActions.OnCopyClicked -> {
            onScreenAction(DashboardActions.OnCopyClicked(action.password))
        }

        is DashBoardItemCardActions.OnMoreClicked -> {
            onScreenAction(DashboardActions.OnMoreClicked(action.cardId))
        }
    }
}

private val PLACEHOLDER_STATS = PasswordStats(
    total = 1,
    safe = 1,
    reused = 1,
    compromised = 1,
    weak = 1
)

@Preview
@Composable
private fun DashBoardScreenContentPreview() {
    DashBoardScreenContent(
        credentials = emptyList(),
        stats = PLACEHOLDER_STATS,
        onAction = {}
    )
}