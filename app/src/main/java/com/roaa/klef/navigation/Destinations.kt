package com.roaa.klef.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destinations : NavKey {

    @Serializable
    data object DashBoardScreen : Destinations

    @Serializable
    data class PasswordInfoScreen(val passwordId: String) : Destinations
    @Serializable
    data object PasswordHealthScreen : Destinations

    @Serializable
    data object PasswordGenerateScreen : Destinations

    /**
     * Single editor route for add and edit.
     * - editingId == null  → adding a new password
     * - editingId != null  → editing an existing password
     * - prefilledPassword  → optional, used by the "save generated password" flow
     */
    @Serializable
    data class PasswordEditorScreen(
        val editingId: String? = null,
        val prefilledPassword: String = ""
    ) : Destinations
}