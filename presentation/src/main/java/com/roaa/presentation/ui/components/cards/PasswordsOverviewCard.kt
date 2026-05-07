package com.roaa.presentation.ui.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.roaa.presentation.R
import com.roaa.presentation.ui.theme.blueFaintBackground
import com.roaa.presentation.ui.theme.compromiseColor
import com.roaa.presentation.ui.theme.reusedColor
import com.roaa.presentation.ui.theme.safeColor
import com.roaa.presentation.ui.theme.weakColor
import com.roaa.presentation.utils.models.PasswordStats

private val CardCornerRadius = 32.dp
private val CardPadding = 16.dp
private val IconCircleIconPadding = 12.dp
private val IconToCountSpacing = 8.dp
private val StatCircleSize = 64.dp
private val StatCircleBorderWidth = 2.dp
private val IllustrationBottomOverflow = (28).dp
private val IllustrationStartInset = 6.dp

val circleAndValueMargin = 4.dp
val circleOverlapMargin = -16.dp

@Composable
fun PasswordOverviewCard(
    stats: PasswordStats,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CardPadding),
            verticalAlignment = Alignment.Top
        ) {
            OverviewLeftColumn(
                totalPasswords = stats.total,
                modifier = Modifier.weight(1f)
            )
            StatInfoColumn(stats = stats)
        }
    }
}

@Composable
private fun OverviewLeftColumn(
    totalPasswords: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        IconBadge(
            iconRes = R.drawable.globe_icon,
            backgroundColor = blueFaintBackground
        )
        Spacer(Modifier.height(IconToCountSpacing))
        Text(
            text = stringResource(R.string.overview_total_passwords, totalPasswords),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Image(
            painter = painterResource(R.drawable.people_illustration),
            contentDescription = null,
            modifier = Modifier.offset(
                x = IllustrationStartInset,
                y = IllustrationBottomOverflow
            )
        )
    }
}

@Composable
private fun IconBadge(
    @DrawableRes iconRes: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.padding(IconCircleIconPadding)
        )
    }
}

@Composable
private fun StatInfoColumn(
    stats: PasswordStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        StatInfoCard(stats = stats)
    }
}

@Composable
fun StatInfoCard(
    stats: PasswordStats,
    modifier: Modifier = Modifier
) {
    val sizeModifier = Modifier.size(64.dp)
    ConstraintLayout(
        modifier = modifier
    ) {
        val (safe, reused, compro, weak) = createRefs()
        val (safeValue, reusedValue, comproValue, weakValue) = createRefs()
        StatCircle(
            modifier = sizeModifier.constrainAs(safe) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                start.linkTo(parent.start)
            },
            color = safeColor,
            label = stringResource(R.string.overview_stat_safe)
        )
        StatValueText(
            modifier = Modifier.constrainAs(safeValue) {
                start.linkTo(safe.end, circleAndValueMargin)
                end.linkTo(parent.end)
                top.linkTo(safe.top)
                bottom.linkTo(safe.bottom)
            },
            text = stats.total.toString(),
            color = safeColor
        )
        StatCircle(
            modifier = sizeModifier.constrainAs(reused) {
                top.linkTo(safe.bottom, circleOverlapMargin)
                end.linkTo(parent.end)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            color = reusedColor,
            label = stringResource(R.string.overview_stat_reused)
        )
        StatValueText(
            modifier = Modifier.constrainAs(reusedValue) {
                end.linkTo(reused.start, circleAndValueMargin)
                start.linkTo(parent.start)
                top.linkTo(reused.top)
                bottom.linkTo(reused.bottom)
            },
            text = stats.reused.toString(),
            color = reusedColor
        )
        StatCircle(
            modifier = sizeModifier.constrainAs(compro) {
                top.linkTo(reused.bottom, circleOverlapMargin)
                end.linkTo(parent.end)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            color = compromiseColor,
            label = stringResource(R.string.overview_stat_compromised)
        )
        StatValueText(
            modifier = Modifier.constrainAs(comproValue) {
                start.linkTo(compro.end, circleAndValueMargin)
                end.linkTo(parent.end)
                top.linkTo(compro.top)
                bottom.linkTo(compro.bottom)
            },
            text = "934",
            color = compromiseColor
        )
        StatValueText(
            modifier = Modifier.constrainAs(weakValue) {
                end.linkTo(weak.start, circleAndValueMargin)
                start.linkTo(parent.start)
                top.linkTo(weak.top)
                bottom.linkTo(weak.bottom)
            },
            text = stats.weak.toString(),
            color = weakColor
        )
        StatCircle(
            modifier = sizeModifier.constrainAs(weak) {
                top.linkTo(compro.bottom, circleOverlapMargin)
                end.linkTo(parent.end)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            color = weakColor,
            label = stringResource(R.string.overview_stat_weak)
        )
    }
}

@Composable
private fun StatCircle(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(StatCircleSize)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape)
            .border(StatCircleBorderWidth, color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun StatValueText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}

@Preview
@Composable
private fun PasswordOverviewCardPreview() {
    PasswordOverviewCard(
        stats = PasswordStats(total = 146, safe = 58, reused = 2, compromised = 934, weak = 85),
        onClick = {}
    )
}

@Preview
@Composable
private fun StatInfoColumnPreview() {
    StatInfoColumn(
        stats = PasswordStats(total = 146, safe = 58, reused = 2, compromised = 934, weak = 85)
    )
}