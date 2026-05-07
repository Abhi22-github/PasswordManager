package com.roaa.data.repository

import com.roaa.domain.repositoryInterfaces.BrandLogoUrlBuilder
import javax.inject.Inject
import javax.inject.Singleton
import com.roaa.data.BuildConfig

@Singleton
class LogoUrlBuilder @Inject constructor() : BrandLogoUrlBuilder {

    override fun forServiceName(serviceName: String): String {
        return "$BASE_URL/$serviceName?token=${BuildConfig.LOGO_API_TOKEN}&size=$DEFAULT_SIZE"
//        return "$BASE_URL/$serviceName?c=1idcGg7MBb6E2QfymEL"
    }

    private companion object {
        const val BASE_URL = "https://img.logo.dev/name"
//        const val BASE_URL = "https://cdn.brandfetch.io"
        const val DEFAULT_SIZE = 128
    }
}