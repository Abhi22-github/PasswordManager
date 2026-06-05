package com.roaa.presentation.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roaa.domain.model.ServiceType
import com.roaa.presentation.R
import com.roaa.presentation.ui.components.appBar.GeneralTopAppBar
import com.roaa.presentation.ui.components.bottomSheets.AppPickerBottomSheet
import com.roaa.presentation.ui.components.buttons.*
import com.roaa.presentation.ui.components.inputs.LabeledTextField
import com.roaa.presentation.ui.components.popup.AutoCompletePopup
import com.roaa.presentation.ui.components.progress.SegmentedStrengthMeter
import com.roaa.presentation.ui.theme.ToolbarBottomGap
import com.roaa.presentation.utils.*
import com.roaa.presentation.utils.formEditor.EditorMode
import com.roaa.presentation.utils.models.*
import com.roaa.presentation.viewModels.*
import kotlinx.coroutines.*

private val PasswordToStrengthSpacing = 13.dp
private val NotesTopExtraSpacing = 6.dp
private val CopyButtonSpacing = 6.dp
private val StrengthBarSpacing = 12.dp
private const val COPY_TRANSITION_MS = 300
private val InputCornerRadius = 20.dp
private val SectionSpacing = 24.dp
private val CloseTextFieldSpacing = 4.dp
private val margin_four = 4.dp
private val margin_twelve = 12.dp
private val margin_eight = 8.dp
private const val COPIED_FEEDBACK_DURATION_MS = 3000L
private const val STRONG_PASSWORD_LENGTH = 16


@Composable
fun PasswordEditorScreen(
    editingId: String?,
    prefilledPassword: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    passwordViewModel: PasswordViewModel = hiltViewModel(),
    brandInfoViewModel: BrandInfoViewModel = hiltViewModel()
) {
    val uiState by passwordViewModel.editorUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val queryUiState by brandInfoViewModel.queryUiState.collectAsStateWithLifecycle()

    DisposableEffect(editingId, prefilledPassword) {
        passwordViewModel.initialize(editingId, prefilledPassword)
        onDispose {
            passwordViewModel.resetEditorState()
        }
    }

    val invalidFormMessage = stringResource(R.string.bookmark_validation_error)
    val recordSavedMessage = stringResource(R.string.bookmark_record_saved_message)

    LaunchedEffect(uiState.saveResult) {
        when (val result = uiState.saveResult) {
            SaveResult.Success -> {
                passwordViewModel.consumeSaveResult()
                onBackClick()
            }

            is SaveResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                passwordViewModel.consumeSaveResult()
            }

            null -> Unit
        }
    }

    LaunchedEffect(uiState.loadError) {
        if (uiState.loadError != null) {
            Toast.makeText(context, uiState.loadError, Toast.LENGTH_SHORT).show()
            onBackClick()
        }
    }

    PasswordEditorScreenContent(
        mode = uiState.mode,
        form = uiState.form,
        isSaving = uiState.isSaving,
        onFormChange = passwordViewModel::onFormChange,
        onQueryChange = brandInfoViewModel::onQueryChanged,
        onBackClick = onBackClick,
        onSave = {
            passwordViewModel.save()
        },
        brandSearchResults = queryUiState,
        modifier = modifier,
        clearSuggestions = brandInfoViewModel::clearSuggestions
    )
}


@Composable
private fun PasswordEditorScreenContent(
    mode: EditorMode?,
    form: BookmarkFormState,
    isSaving: Boolean,
    onFormChange: (BookmarkFormState) -> Unit,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    brandSearchResults: BrandSearchUiState,
    clearSuggestions: () -> Unit
) {
    val title = stringResource(
        when (mode) {
            is EditorMode.Edit -> R.string.editor_title_edit
            is EditorMode.Add, null -> R.string.editor_title_add
        }
    )

    val passwordStrengthObject by remember(form.password) {
        derivedStateOf { calculatePasswordStrength(form.password) }
    }

    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    var fieldFocused by remember { mutableStateOf(false) }
    var autoCompletePopupVisibility by remember { mutableStateOf(false) }
    var isCopied by remember { mutableStateOf(false) }
    val copyPassword = rememberPasswordClipboard()
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<InstalledApp?>(null) }

    LaunchedEffect(form.password) {
        // Typing in the password resets the copy-feedback state.
        isCopied = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (showSheet) {
            AppPickerBottomSheet(
                onDismiss = { showSheet = false },
                onAppSelected = { app ->
                    selectedApp = app
                    onFormChange(form.copy(serviceName = app.name, serviceType = ServiceType.APP))
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            GeneralTopAppBar(onBackButtonClicked = onBackClick, title = title)
            Spacer(Modifier.height(SectionSpacing))

            Box {
                LabeledTextField(
                    label = stringResource(R.string.bookmark_label_service),
                    placeholder = stringResource(R.string.bookmark_placeholder_service),
                    value = form.serviceName,
                    isLoading = brandSearchResults.isLoading,
                    onValueChange = {
                        autoCompletePopupVisibility = true
                        onQueryChange(it)
                        onFormChange(form.copy(serviceName = it))
                    },
                    modifier = Modifier
                        .onSizeChanged { fieldSize = it }
                        .onFocusChanged { fieldFocused = it.isFocused },
                    shape = RoundedCornerShape(InputCornerRadius),
                    isDisable = selectedApp != null
                )

                if (brandSearchResults.suggestions.isNotEmpty() && fieldFocused && autoCompletePopupVisibility) {
                    AutoCompletePopup(
                        modifier = Modifier,
                        fieldSize = fieldSize,
                        brandSearchResults = brandSearchResults,
                        brandItemClick = {
                            autoCompletePopupVisibility = false
                            clearSuggestions()
                            onFormChange(
                                form.copy(
                                    domainName = it.domain,
                                    logoUrl = it.logoUrl,
                                    serviceName = it.name
                                )
                            )
                        }
                    )
                }
            }
            Spacer(Modifier.height(margin_eight))
            AnimatedContent(
                targetState = selectedApp == null,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) {
                if (it) {
                    FilledTonalButton(
                        onClick = {
                            showSheet = !showSheet
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.apps_icon),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.width(margin_four))
                        Text(
                            text = "Pick app",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            AnimatedContent(
                targetState = selectedApp == null,
                transitionSpec = {
                    slideInVertically(initialOffsetY = { -it }) togetherWith slideOutVertically(
                        targetOffsetY = { it })
                }
            ) {
                if (!it) {
                    selectedApp?.let {
                        SelectedAppCard(
                            modifier = Modifier,
                            selectedApp = it,
                            removeSelectedApp = {
                                selectedApp = null
                                onFormChange(
                                    form.copy(
                                        serviceName = "",
                                        serviceType = ServiceType.WEBSITE
                                    )
                                )
                            }
                        )
                    }
                }
            }


            Spacer(Modifier.height(SectionSpacing))

            LabeledTextField(
                label = stringResource(R.string.editor_label_username),
                placeholder = stringResource(R.string.editor_placeholder_username),
                value = form.username,
                keyboardType = KeyboardType.Email,
                onValueChange = { onFormChange(form.copy(username = it)) },
                shape = RoundedCornerShape(
                    topStart = InputCornerRadius,
                    topEnd = InputCornerRadius
                ),
            )
            Spacer(Modifier.height(CloseTextFieldSpacing))
            Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                LabeledTextField(
                    modifier = Modifier.weight(0.8f),
                    label = stringResource(R.string.editor_label_password),
                    placeholder = stringResource(R.string.editor_placeholder_password),
                    value = form.password,
                    keyboardType = KeyboardType.Password,
                    onValueChange = { onFormChange(form.copy(password = it)) },
                    shape = RoundedCornerShape(
                        bottomStart = InputCornerRadius
                    ),
                    isPasswordTextField = true
                )
                Spacer(Modifier.width(CloseTextFieldSpacing))
                CopyOutlineButton(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight(),
                    isCopied = isCopied,
                    shape = RoundedCornerShape(bottomEnd = InputCornerRadius),
                    onClick = {
                        copyPassword(form.password)
                        isCopied = true
                        scope.launch {
                            delay(COPIED_FEEDBACK_DURATION_MS)
                            isCopied = false
                        }
                    }
                )
            }


            PasswordFieldDetails(
                passwordStrengthObject = passwordStrengthObject,
                suggestPasswordButtonClicked = {
                    onFormChange(
                        form.copy(
                            password = generatePassword(
                                STRONG_PASSWORD_LENGTH,
                                includeDigits = true,
                                includeSpecialChars = true,
                                includeUppercase = true
                            )
                        )
                    )
                }
            )

            Spacer(Modifier.height(SectionSpacing))
            LabeledTextField(
                label = stringResource(R.string.editor_label_notes),
                placeholder = stringResource(R.string.editor_placeholder_notes),
                value = form.notes ?: "",
                onValueChange = { onFormChange(form.copy(notes = it)) },
                singleLine = false,
                modifier = Modifier.padding(top = NotesTopExtraSpacing),
                shape = RoundedCornerShape(InputCornerRadius),
                minLines = 4,
                maxLines = 4,
            )

            Spacer(Modifier.height(ToolbarBottomGap))
        }

        SaveButton(
            onClick = onSave,
            enabled = form.isValid && !isSaving && mode != null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun PasswordFieldDetails(
    passwordStrengthObject: PasswordStrengthObject,
    modifier: Modifier = Modifier,
    suggestPasswordButtonClicked: () -> Unit
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(margin_twelve))
        SegmentedStrengthMeter(
            passwordStrengthObject = passwordStrengthObject,
            modifier = Modifier.padding(horizontal = margin_four)
        )
        Spacer(Modifier.height(margin_eight))
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledTonalButton(
                onClick = suggestPasswordButtonClicked,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.suggest_password_icon),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(margin_four))
                Text(
                    text = "Suggest strong password",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun SelectedAppCard(
    modifier: Modifier = Modifier,
    selectedApp: InstalledApp,
    removeSelectedApp: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        shape = RoundedCornerShape(InputCornerRadius)
    ) {
        Row(
            modifier = Modifier.padding(margin_twelve),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = remember(selectedApp) {
                        selectedApp.icon.toBitmap(width = 96, height = 96)
                            .asImageBitmap()
                    },
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(margin_eight))
                Text(
                    text = selectedApp.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            FilledIconButton(
                modifier = Modifier,
                onClick = removeSelectedApp,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Icon(
                    Icons.Rounded.Close, contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}


@Composable
private fun CopyOutlineButton(
    isCopied: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape
) {
    AnimatedContent(
        targetState = isCopied,
        transitionSpec = {
            (fadeIn(tween(COPY_TRANSITION_MS)) + scaleIn(
                initialScale = 0.85f,
                animationSpec = tween(COPY_TRANSITION_MS)
            )) togetherWith
                    (fadeOut(tween(COPY_TRANSITION_MS)) + scaleOut(
                        targetScale = 0.85f,
                        animationSpec = tween(COPY_TRANSITION_MS)
                    ))
        },
        label = "CopyButton",
        modifier = Modifier
    ) { copied ->
        OutlineButton(
            onClick = onClick,
            icon = if (copied) R.drawable.copied_check_icon else R.drawable.copy_icon,
            shape = shape,
            modifier = modifier,
            contentDescription = stringResource(
                if (copied) R.string.bookmark_copied else R.string.bookmark_copy_password
            ),
            backgroundColor = if (copied) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
            iconTint = if (copied) {
                MaterialTheme.colorScheme.onSecondary
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer
            },
            outlineColor = Color.Transparent   // ← drop the outline entirely
        )
    }
}

@Composable
private fun StrengthIndicator(
    score: Float,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { score },
            modifier = Modifier.weight(1f),
            color = color
        )
        Spacer(Modifier.width(StrengthBarSpacing))
        Text(text = label, color = color)
    }
}


@Preview
@Composable
private fun PasswordEditorScreenContentAddPreview() {
    PasswordEditorScreenContent(
        mode = EditorMode.Add(prefilledPassword = ""),
        form = BookmarkFormState(),
        isSaving = false,
        onFormChange = {},
        onQueryChange = {},
        onBackClick = {},
        onSave = {},
        brandSearchResults = BrandSearchUiState(),
        clearSuggestions = {}
    )
}

@Preview
@Composable
private fun PasswordEditorScreenContentEditPreview() {
    PasswordEditorScreenContent(
        mode = null, // simulates loading state
        form = BookmarkFormState(
            serviceName = "Facebook",
            username = "user@example.com",
            password = "P@s5w0rdEx!m",
            notes = ""
        ),
        isSaving = false,
        onFormChange = {},
        onQueryChange = {},
        onBackClick = {},
        onSave = {},
        brandSearchResults = BrandSearchUiState(),
        clearSuggestions = {}
    )
}