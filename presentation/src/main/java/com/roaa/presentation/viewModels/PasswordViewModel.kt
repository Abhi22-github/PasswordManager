package com.roaa.presentation.viewModels

import androidx.lifecycle.*
import com.roaa.domain.model.*
import com.roaa.domain.repositoryInterfaces.BrandLogoUrlBuilder
import com.roaa.domain.usecase.*
import com.roaa.presentation.utils.PasswordInfoUiState
import com.roaa.presentation.utils.formEditor.EditorMode
import com.roaa.presentation.utils.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val addPassword: AddPasswordUseCase,
    private val updatePassword: UpdatePasswordUseCase,
    private val getPasswordById: GetPasswordByIdUseCase,
    private val getAllPasswords: GetAllPasswordsUseCase,
    private val deletePasswordById: DeletePasswordByIdUseCase,
    private val observePasswordById: ObservePasswordByIdUseCase,
    private val logoUrlBuilder: BrandLogoUrlBuilder
) : ViewModel() {


    private val _recentlyCopied = MutableStateFlow<List<Credentials>>(emptyList())
    val recentlyCopied: StateFlow<List<Credentials>> = _recentlyCopied.asStateFlow()

    fun onPasswordCopied(credentials: Credentials) {
        _recentlyCopied.update { current ->
            (listOf(credentials) + current.filter { it.id != credentials.id }).take(10)
        }
    }

    private val _passwordInfoUiState = MutableStateFlow(PasswordInfoUiState())
    val passwordInfoUiState: StateFlow<PasswordInfoUiState> = _passwordInfoUiState.asStateFlow()

    private var observeJob: Job? = null

    fun loadPassword(passwordId: String) {
        // If we're already observing this ID, do nothing.
        if (_passwordInfoUiState.value.credentials?.id == passwordId && observeJob?.isActive == true) {
            return
        }

        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _passwordInfoUiState.update { it.copy(isLoading = true, notFound = false) }

            observePasswordById.invoke(passwordId).collect { password ->
                _passwordInfoUiState.update {
                    it.copy(
                        isLoading = false,
                        credentials = password,
                        notFound = password == null,
                        logoImageUrl = password?.logoUrl
                    )
                }
            }
        }
    }

    private val _editorUiState = MutableStateFlow(EditorUiState())
    val editorUiState: StateFlow<EditorUiState> = _editorUiState.asStateFlow()

    /**
     * Get all password list from database.
     */
    // null = still loading, emptyList = loaded but no entries, non-empty = has entries
    val allPasswords: StateFlow<List<Credentials>?> = getAllPasswords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    /**
     * Delete and password by ID.
     */
    fun deletePasswordById(passwordId: String) = viewModelScope.launch {
        deletePasswordById.invoke(passwordId)
    }

    /**
     * Method to create an entry in database of password.
     */
    fun addPassword(
        serviceName: String,
        domainName: String,
        username: String,
        password: String,
        logoUrl: String,
        websiteUrl: String,
        notes: String,
        strength: Float,
        serviceType: ServiceType
    ) {
        viewModelScope.launch {
            addPassword.invoke(
                serviceName = serviceName,
                domainName = domainName,
                username = username,
                logoUrl = logoUrl,
                password = password,
                websiteUrl = websiteUrl,
                notes = notes,
                strength = strength,
                serviceType = serviceType
            )
        }
    }

    /**
     * Called once by the screen on first composition. Loads the existing
     * record for edit mode; for add mode, just records the prefilled password.
     * No-op if already initialized (survives config-change recomposition).
     */
    fun initialize(editingId: String?, prefilledPassword: String) {
        if (_editorUiState.value.mode != null) return

        if (editingId == null) {
            _editorUiState.update {
                it.copy(
                    mode = EditorMode.Add(prefilledPassword),
                    form = BookmarkFormState(password = prefilledPassword)
                )
            }
            return
        }

        viewModelScope.launch {
            val existing = runCatching { getPasswordById.invoke(editingId) }.getOrNull()

            if (existing == null) {
                _editorUiState.update { it.copy(loadError = "Password not found") }
                return@launch
            }

            _editorUiState.update {
                it.copy(
                    mode = EditorMode.Edit(editingId, existing),
                    form = BookmarkFormState.fromPassword(existing)
                )
            }
        }
    }

    fun resetEditorState() {
        _editorUiState.value = EditorUiState()
    }

    fun onFormChange(form: BookmarkFormState) {
        _editorUiState.update { it.copy(form = form) }
    }

    fun save() {
        val state = _editorUiState.value
        val mode = state.mode ?: return

        if (!state.form.isValid) {
            _editorUiState.update {
                it.copy(saveResult = SaveResult.Error("Please enter valid data"))
            }
            return
        }

        viewModelScope.launch {
            _editorUiState.update { it.copy(isSaving = true) }

            val result = runCatching {
                when (mode) {
                    is EditorMode.Add -> commitAdd(state.form)
                    is EditorMode.Edit -> commitEdit(mode, state.form)
                }
            }

            _editorUiState.update {
                it.copy(
                    isSaving = false,
                    saveResult = result.fold(onSuccess = {
                        SaveResult.Success
                    }, onFailure = { e ->
                        SaveResult.Error(e.message ?: "Save failed")
                    })
                )
            }
        }
    }

    fun consumeSaveResult() {
        _editorUiState.update { it.copy(saveResult = null) }
    }

    private suspend fun commitAdd(form: BookmarkFormState) {
        addPassword.invoke(
            serviceName = form.serviceName,
            domainName = form.domainName,
            username = form.username,
            logoUrl = form.logoUrl,
            password = form.password,
            notes = form.notes,
            //TODO
            websiteUrl = "",
            strength = form.passwordStrength,
            serviceType = form.serviceType
        )
    }

    private suspend fun commitEdit(mode: EditorMode.Edit, form: BookmarkFormState) {
        if (form.matches(mode.original)) return

        updatePassword.invoke(
            (mode.original.copy(
                serviceName = form.serviceName,
                username = form.username,
                password = form.password,
                notes = form.notes,
                strength = form.passwordStrength
            ))
        )
    }
}