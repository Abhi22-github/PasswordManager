package com.roaa.presentation.utils.models

import com.roaa.domain.model.BrandSuggestion

data class BrandSearchUiState(
    val isLoading: Boolean = false,
    val suggestions: List<BrandSuggestion> = emptyList(),
    val error: String? = null
)