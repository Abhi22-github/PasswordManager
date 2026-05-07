package com.roaa.domain.repositoryInterfaces

interface BrandLogoUrlBuilder {
    fun forServiceName(serviceName: String): String?
}