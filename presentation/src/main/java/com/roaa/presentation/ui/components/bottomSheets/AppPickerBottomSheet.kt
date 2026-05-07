package com.roaa.presentation.ui.components.bottomSheets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.roaa.presentation.utils.InstalledAppsProvider.getInstalledApps
import com.roaa.presentation.utils.models.InstalledApp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAppSelected: (InstalledApp) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var allApps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        allApps = getInstalledApps(context)
        isLoading = false
    }

    val filteredApps by remember {
        derivedStateOf {
            if (query.isBlank()) allApps
            else allApps.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.packageName.contains(query, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(query) {
        if (filteredApps.isNotEmpty()) listState.scrollToItem(0)
    }

    fun close() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 8.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Select App",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                )
                //TextButton(onClick = ::close) { Text("Cancel") }
            }

//            OutlinedTextField(
//                value = query,
//                onValueChange = { query = it },
//                placeholder = { Text("Search apps") },
//                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
//                trailingIcon = {
//                    if (query.isNotEmpty()) {
//                        IconButton(onClick = { query = "" }) {
//                            Icon(Icons.Outlined.Close, contentDescription = "Clear")
//                        }
//                    }
//                },
//                singleLine = true,
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
//                shape = RoundedCornerShape(28.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 4.dp)
//            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 500.dp)
            ) {
                when {
                    isLoading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )

                    filteredApps.isEmpty() -> Text(
                        text = if (query.isNotBlank()) "No apps match \"$query\"" else "No apps found",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    else -> LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(items = filteredApps, key = { it.packageName }) { app ->
                            AppListItem(app = app, onClick = {
                                onAppSelected(app)
                                close()
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppListItem(app: InstalledApp, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = remember(app.packageName) {
                app.icon.toBitmap(width = 96, height = 96).asImageBitmap()
            },
            contentDescription = app.name,
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = app.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )

    }
}