package com.roaa.domain.usecase

import com.roaa.domain.repositoryInterfaces.PasswordRepository
import javax.inject.Inject

class DeletePasswordByIdUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(passwordId: String) {
        repository.deleteById(passwordId)
    }
}