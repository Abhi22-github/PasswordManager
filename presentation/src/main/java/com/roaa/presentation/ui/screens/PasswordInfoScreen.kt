package com.roaa.presentation.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roaa.domain.model.Credentials
import com.roaa.domain.model.ServiceType
import com.roaa.presentation.ui.components.appBar.GeneralTopAppBar
import com.roaa.presentation.ui.components.cards.*
import com.roaa.presentation.utils.calculatePasswordStrength
import com.roaa.presentation.utils.rememberPasswordClipboard
import com.roaa.presentation.viewModels.PasswordViewModel

private val ScreenHorizontalPadding = 16.dp
private val ScreenVerticalPadding = 12.dp
private val TopBarToContentSpacing = 8.dp
private val InfoCardOverlap = (-64).dp

@Composable
fun PasswordInfoScreen(
    passwordId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.passwordInfoUiState.collectAsStateWithLifecycle()

    LaunchedEffect(passwordId) {
        viewModel.loadPassword(passwordId)
    }

    PasswordInfoScreenContent(
        details = uiState.credentials,
        logoImage = uiState.logoImageUrl,
        isLoading = uiState.isLoading,
        onBackClick = onBackClick,
        onEditClick = { uiState.credentials?.id?.let(onEditClick) },
        onShareClick = { /* TODO: trigger share sheet */ },
        onDeleteClick = {
            uiState.credentials?.id?.let { id ->
                viewModel.deletePasswordById(id)
                onBackClick()
            }
        },
        modifier = modifier
    )
}

@Composable
private fun PasswordInfoScreenContent(
    details: Credentials?,
    logoImage: String?,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = ScreenHorizontalPadding,
                vertical = ScreenVerticalPadding
            )
    ) {
        GeneralTopAppBar(onBackButtonClicked = onBackClick)
        Spacer(Modifier.height(TopBarToContentSpacing))

        when {
            isLoading || details == null -> LoadingPlaceholder()
            else -> LoadedContent(
                details = details,
                logoImage = logoImage,
                onEditClick = onEditClick,
                onShareClick = onShareClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun LoadedContent(
    details: Credentials,
    logoImage: String?,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val copyPassword = rememberPasswordClipboard()
    val passwordStrengthObject = calculatePasswordStrength(details.password)

    Column(modifier = modifier) {
        PasswordStrengthCard(progress = passwordStrengthObject.passwordScore)

        PasswordInfoCard(
            serviceName = details.serviceName,
            username = details.username,
            password = details.password,
            serviceIcon = logoImage,
            onEditClick = onEditClick,
            onShareClick = onShareClick,
            onDeleteClick = onDeleteClick,
            onCopyPassword = { copyPassword(details.password) },
            modifier = Modifier.offset(y = InfoCardOverlap)
        )
    }
}

@Composable
private fun LoadingPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CircularProgressIndicator()
    }
}

private val PlaceholderCredentialsDetails = Credentials(
    serviceName = "Facebook",
    username = "user.email@gmail.com",
    password = "Parth@Vyas@146uiux",
    domainName = "Facebook.com",
    id = "1",
    logoUrl = "",
    websiteUrl = "www.Facebook.com",
    notes = "",
    strength = 0.5f,
    createdAt = 0L,
    updatedAt = 0L,
    serviceType = ServiceType.WEBSITE
)

@Preview
@Composable
private fun PasswordInfoScreenContentLoadedPreview() {
    PasswordInfoScreenContent(
        details = PlaceholderCredentialsDetails,
        isLoading = false,
        onBackClick = {},
        onEditClick = {},
        onShareClick = {},
        onDeleteClick = {},
        logoImage = ""
    )
}

@Preview
@Composable
private fun PasswordInfoScreenContentLoadingPreview() {
    PasswordInfoScreenContent(
        details = null,
        isLoading = true,
        onBackClick = {},
        onEditClick = {},
        onShareClick = {},
        onDeleteClick = {},
        logoImage = ""
    )
}