package com.roaa.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val id: String,
    val serviceName: String,
    val domainName: String,
    val username: String,
    val logoUrl: String?,
    val password: String,
    val websiteUrl: String? = null,
    val notes: String? = null,
    val serviceType: ServiceType,
    val strength: Float = 0f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastCopiedAt: Long? = null
)
