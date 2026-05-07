package com.roaa.domain.usecase

import com.roaa.domain.model.Credentials
import com.roaa.domain.repositoryInterfaces.PasswordRepository
import javax.inject.Inject


class GetPasswordByIdUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(id: String): Credentials? = repository.getById(id)
}