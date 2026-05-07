package com.roaa.data.remote.api

import com.roaa.data.BuildConfig
import com.roaa.data.remote.dto.BrandSearchDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BrandFetchApi {

    @GET("v2/search/{query}")
    suspend fun searchBrands(
        @Path("query") query: String,
        @Query("c") clientId: String = BuildConfig.LOGO_API_TOKEN
    ): List<BrandSearchDto>

    companion object {
        const val BASE_URL = "https://api.brandfetch.io/"
        const val LOGO_CDN = "https://cdn.brandfetch.io/"
    }
}