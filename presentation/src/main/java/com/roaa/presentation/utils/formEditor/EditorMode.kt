package com.roaa.presentation.utils.formEditor

import com.roaa.domain.model.Credentials

sealed interface EditorMode {
    data class Add(val prefilledPassword: String) : EditorMode
    data class Edit(val passwordId: String, val original: Credentials) : EditorMode
}