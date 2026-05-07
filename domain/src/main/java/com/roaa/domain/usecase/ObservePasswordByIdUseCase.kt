package com.roaa.domain.usecase

import com.roaa.domain.model.Credentials
import com.roaa.domain.repositoryInterfaces.PasswordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePasswordByIdUseCase @Inject constructor(
    private val repository: PasswordRepository,
) {
    suspend operator fun invoke(passwordId: String): Flow<Credentials?> = repository.observePasswordById(passwordId)
}