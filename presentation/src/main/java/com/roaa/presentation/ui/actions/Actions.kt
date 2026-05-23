package com.roaa.presentation.ui.actions

/**
 * Actions emitted by a single password card on the dashboard list.
 */
sealed interface DashBoardItemCardActions {
    data class OnCardClicked(val passwordId: String) : DashBoardItemCardActions
    data class OnCopyClicked(val password: String) : DashBoardItemCardActions
    data class OnMoreClicked(val cardId: String) : DashBoardItemCardActions
}

/**
 * Actions emitted by the Dashboard screen as a whole.
 */
sealed interface DashboardActions {
    data class OnCardClicked(val passwordId: String) : DashboardActions
    data class OnCopyClicked(val password: String) : DashboardActions
    data class OnMoreClicked(val cardId: String) : DashboardActions
    data object OnAddPasswordClick : DashboardActions
}
