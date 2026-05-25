package com.roaa.presentation.ui.components.toolbar

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roaa.presentation.R
import com.roaa.presentation.ui.theme.ManropeFont
import com.roaa.presentation.utils.BottomAppBarState

private val ToolbarContentPadding = 4.dp
private const val COLOR_ANIM_DURATION_MS = 500
private const val ICON_FADE_DURATION_MS = 300
private const val UNSELECTED_ICON_SCALE = 0.8f
private const val SELECTED_ICON_SCALE = 1f

private data class ToolbarItem(
    val state: BottomAppBarState,
    @DrawableRes val outlineIcon: Int,
    @DrawableRes val filledIcon: Int,
    val labelRes: Int
)

private val ToolbarItems = listOf(
    ToolbarItem(
        state = BottomAppBarState.DashboardScreen,
        outlineIcon = R.drawable.home_outline_icon,
        filledIcon = R.drawable.home_filled_icon,
        labelRes = R.string.toolbar_home
    ),
    ToolbarItem(
        state = BottomAppBarState.HealthScreen,
        outlineIcon = R.drawable.health_outline_icon,
        filledIcon = R.drawable.health_filled_icon,
        labelRes = R.string.toolbar_health
    ),
    ToolbarItem(
        state = BottomAppBarState.PasswordGeneratorScreen,
        outlineIcon = R.drawable.generate_outline_icon,
        filledIcon = R.drawable.generate_filled_icon,
        labelRes = R.string.toolbar_generate
    )
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HorizontalToolbar(
    selectedAppBarScreen: BottomAppBarState,
    onSelectedAppBarScreenChange: (BottomAppBarState) -> Unit,
    onAddPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalFloatingToolbar(
            expanded = true,
            colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            floatingActionButton = {
                AddPasswordFab(onClick = onAddPasswordClick,modifier = Modifier.size(64.dp))
            },
            collapsedShadowElevation =  FloatingToolbarDefaults.ContainerExpandedElevationWithFab,
            contentPadding = PaddingValues(ToolbarContentPadding)
        ) {
            ToolbarItems.forEach { item ->
                ToolbarButton(
                    item = item,
                    isSelected = selectedAppBarScreen == item.state,
                    onClick = { onSelectedAppBarScreenChange(item.state) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddPasswordFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingToolbarDefaults.VibrantFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.add_icon),
            contentDescription = stringResource(R.string.toolbar_add_password),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ToolbarButton(
    item: ToolbarItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        },
        animationSpec = tween(COLOR_ANIM_DURATION_MS),
        label = "ToolbarContainerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(COLOR_ANIM_DURATION_MS),
        label = "ToolbarContentColor"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) SELECTED_ICON_SCALE else UNSELECTED_ICON_SCALE,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ToolbarIconScale"
    )

    val label = stringResource(item.labelRes)

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = containerColor)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Crossfade(
                targetState = isSelected,
                animationSpec = tween(ICON_FADE_DURATION_MS),
                modifier = Modifier.scale(iconScale),
                label = "ToolbarIconCrossfade"
            ) { selected ->
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if (selected) item.filledIcon else item.outlineIcon
                    ),
                    contentDescription = label,
                    tint = contentColor
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                fontFamily = ManropeFont,
                color = contentColor
            )
        }
    }
}

@Preview
@Composable
private fun HorizontalToolbarHomeSelectedPreview() {
    HorizontalToolbar(
        selectedAppBarScreen = BottomAppBarState.DashboardScreen,
        onSelectedAppBarScreenChange = {},
        onAddPasswordClick = {}
    )
}

@Preview
@Composable
private fun HorizontalToolbarHealthSelectedPreview() {
    HorizontalToolbar(
        selectedAppBarScreen = BottomAppBarState.HealthScreen,
        onSelectedAppBarScreenChange = {},
        onAddPasswordClick = {}
    )
}


@Preview
@Composable
private fun HorizontalToolbarGenerateSelectedPreview() {
    HorizontalToolbar(
        selectedAppBarScreen = BottomAppBarState.PasswordGeneratorScreen,
        onSelectedAppBarScreenChange = {},
        onAddPasswordClick = {}
    )
}