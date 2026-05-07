package com.roaa.data.repository

import com.roaa.data.BuildConfig
import com.roaa.data.remote.api.BrandFetchApi
import com.roaa.domain.model.BrandSuggestion
import com.roaa.domain.repositoryInterfaces.BrandRepository
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val api: BrandFetchApi
) : BrandRepository {

    override suspend fun searchBrands(query: String): Result<List<BrandSuggestion>> =
        runCatching {
            api.searchBrands(query.trim()).map { dto ->
                BrandSuggestion(
                    name = dto.name,
                    domain = dto.domain,
                    iconUrl = dto.icon,
                    logoUrl = "${BrandFetchApi.LOGO_CDN}${dto.domain}" +
                            "?c=${BuildConfig.LOGO_API_TOKEN}"
                )
            }
        }
}