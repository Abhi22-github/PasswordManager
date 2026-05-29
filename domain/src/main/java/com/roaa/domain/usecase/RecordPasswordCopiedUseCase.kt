package com.roaa.domain.usecase

import com.roaa.domain.repositoryInterfaces.PasswordRepository
import javax.inject.Inject

class RecordPasswordCopiedUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(id: String) = repository.recordPasswordCopied(id)
}
