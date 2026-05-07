package com.roaa.domain.usecase

import com.roaa.domain.model.Credentials
import com.roaa.domain.repositoryInterfaces.PasswordRepository
import javax.inject.Inject

class DeletePasswordUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(credentials: Credentials) {
        repository.deletePassword(credentials)
    }
}