package com.roaa.presentation.ui.components.popup

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import coil3.compose.AsyncImage
import com.roaa.domain.model.BrandSuggestion
import com.roaa.presentation.utils.models.BrandSearchUiState

@Composable
fun AutoCompletePopup(
    modifier: Modifier = Modifier,
    fieldSize: IntSize,
    brandSearchResults: BrandSearchUiState,
    brandItemClick: (BrandSuggestion) -> Unit
) {

    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(0, fieldSize.height),
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .width(with(LocalDensity.current) { fieldSize.width.toDp() })
                .heightIn(max = 280.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            LazyColumn {
                itemsIndexed(
                    brandSearchResults.suggestions,
                    key = { _, item -> item.domain }
                ) { index, brand ->
                    BrandSuggestionItem(
                        brand = brand,
                        onClick = {
                            brandItemClick(brand)
                        },
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(200),
                            fadeOutSpec = tween(150),
                            placementSpec = spring(0.85f, 200f)
                        )
                    )
                }
            }
        }
    }
}


@Composable
private fun BrandSuggestionItem(
    brand: BrandSuggestion,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = brand.logoUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                brand.name, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                brand.domain,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}