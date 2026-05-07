package com.roaa.domain.usecase

import com.roaa.domain.model.Credentials
import com.roaa.domain.repositoryInterfaces.PasswordRepository
import javax.inject.Inject


class UpdatePasswordUseCase @Inject constructor(
    private val repository: PasswordRepository,
) {
    suspend operator fun invoke(credentials: Credentials): Result<Unit> = runCatching {
        require(credentials.serviceName.isNotBlank()) { "Service name is required" }
        require(credentials.password.isNotBlank()) { "Password is required" }

        val updated = credentials.copy(
            updatedAt = System.currentTimeMillis()
        )
        repository.updatePassword(updated)
    }
}