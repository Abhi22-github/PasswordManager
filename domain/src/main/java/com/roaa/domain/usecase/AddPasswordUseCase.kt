package com.roaa.domain.usecase

import com.roaa.domain.model.Credentials
import com.roaa.domain.model.ServiceType
import com.roaa.domain.repositoryInterfaces.PasswordRepository
import java.util.UUID
import javax.inject.Inject

class AddPasswordUseCase @Inject constructor(
    private val repository: PasswordRepository,
) {
    suspend operator fun invoke(
        serviceName: String,
        domainName: String,
        username: String,
        password: String,
        logoUrl: String,
        websiteUrl: String? = null,
        notes: String? = null,
        strength: Float,
        serviceType: ServiceType
    ): Result<Unit> = runCatching {
        require(serviceName.isNotBlank()) { "Service name is required" }
        require(username.isNotBlank()) { "Username is required" }
        require(password.isNotBlank()) { "Password is required" }

        val now = System.currentTimeMillis()
        val newCredentials = Credentials(
            id = UUID.randomUUID().toString(),
            serviceName = serviceName.trim(),
            domainName = domainName.trim(),
            logoUrl = logoUrl.trim(),
            username = username.trim(),
            password = password,
            websiteUrl = websiteUrl?.trim()?.takeIf { it.isNotEmpty() },
            notes = notes?.trim()?.takeIf { it.isNotEmpty() },
            strength = strength,
            createdAt = now,
            updatedAt = now,
            serviceType = serviceType
        )

        repository.addPassword(newCredentials)
    }
}