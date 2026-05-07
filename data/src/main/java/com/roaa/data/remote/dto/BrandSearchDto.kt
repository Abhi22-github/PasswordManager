package com.roaa.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrandSearchDto(
    @SerialName("name") val name: String,
    @SerialName("domain") val domain: String,
    @SerialName("icon") val icon: String? = null,        // small icon URL
    @SerialName("brandId") val brandId: String? = null
)