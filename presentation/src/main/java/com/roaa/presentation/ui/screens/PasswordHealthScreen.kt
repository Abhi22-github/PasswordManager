package com.roaa.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roaa.presentation.ui.theme.*

@Composable
fun PasswordHealthScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
    PasswordHealthScreenContent(
        modifier = modifier
    )
}

@Composable
fun PasswordHealthScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VerticalSpacingInConnectedCards)
    ) {
        repeat(3) {
            PasswordHealthItemCard()
        }
    }
}

@Composable
fun PasswordHealthItemCard(modifier: Modifier = Modifier) {
    GenericCardContainer {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(0.8f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(48.dp))
                        .background(blue), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Info, contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column() {
                    Text(text = "59 Passwords compromized")
                    Text(text = "resolve now")
                }
            }
            IconButton(onClick = {}, modifier = Modifier.weight(0.1f)) {
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
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(GlobalCardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(CardInnerPadding),
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun PasswordHealthScreenContentPreview() {
    PasswordHealthScreenContent()
}