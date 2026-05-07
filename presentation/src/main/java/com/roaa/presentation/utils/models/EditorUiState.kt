package com.roaa.presentation.utils.models

import com.roaa.presentation.utils.formEditor.EditorMode


data class EditorUiState(
    val mode: EditorMode? = null,
    val form: BookmarkFormState = BookmarkFormState(),
    val isSaving: Boolean = false,
    val saveResult: SaveResult? = null,
    val loadError: String? = null
)

sealed interface SaveResult {
    data object Success : SaveResult
    data class Error(val message: String) : SaveResult
}