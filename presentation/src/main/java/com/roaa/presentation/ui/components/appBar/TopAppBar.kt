package com.roaa.presentation.ui.components.appBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.roaa.presentation.R
import com.roaa.presentation.ui.components.buttons.CircleWhiteButton

private val TopAppBarHeight: Dp = 64.dp
private val FilledButtonSize = 48.dp
private val margin_twelve = 12.dp

@Composable
fun DashBoardTopAppBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {}
) {
    TopAppBarContainer(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.klef_icon),
            contentDescription = "",
            modifier = Modifier.size(FilledButtonSize)
        )
        CircleWhiteButton(
            onClick = onSettingsClick,
            icon = R.drawable.settings_icon,
            modifier = Modifier.size(FilledButtonSize)
        )
    }
}

@Composable
fun GeneralTopAppBar(
    modifier: Modifier = Modifier,
    onBackButtonClicked: () -> Unit,
    title: String? = null
) {
    TopAppBarContainer(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ) {
        CircleWhiteButton(
            onClick = onBackButtonClicked,
            icon = R.drawable.back_icon,
            modifier = Modifier.size(FilledButtonSize)
        )
        title?.let {
            Spacer(modifier = Modifier.width(margin_twelve))
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TopAppBarContainer(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(TopAppBarHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        content()
    }
}

@Preview
@Composable
private fun DashBoardTopAppBarPreview() {
    DashBoardTopAppBar()
}

@Preview
@Composable
private fun GeneralTopAppBarPreview() {
    GeneralTopAppBar(onBackButtonClicked = {}, title = "Add Password")
}