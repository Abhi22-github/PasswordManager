package com.roaa.domain.repositoryInterfaces

import com.roaa.domain.model.BrandSuggestion

interface BrandRepository {
    suspend fun searchBrands(query: String): Result<List<BrandSuggestion>>
}