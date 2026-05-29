package com.roaa.domain.usecase

import com.roaa.domain.model.Credentials
import com.roaa.domain.repositoryInterfaces.PasswordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentlyCopiedUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    operator fun invoke(): Flow<List<Credentials>> = repository.getRecentlyCopied()
}
