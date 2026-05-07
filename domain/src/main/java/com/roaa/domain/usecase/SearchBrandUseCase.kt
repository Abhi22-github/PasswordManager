package com.roaa.domain.usecase

import com.roaa.domain.model.BrandSuggestion
import com.roaa.domain.repositoryInterfaces.BrandRepository
import javax.inject.Inject

class SearchBrandsUseCase @Inject constructor(
    private val repository: BrandRepository
) {
    suspend operator fun invoke(query: String): Result<List<BrandSuggestion>> {
        if (query.isBlank() || query.length < 2) {
            return Result.success(emptyList())
        }
        return repository.searchBrands(query)
    }
}
