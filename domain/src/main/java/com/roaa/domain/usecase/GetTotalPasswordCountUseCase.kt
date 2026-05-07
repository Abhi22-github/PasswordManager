package com.roaa.domain.usecase

import com.roaa.domain.repositoryInterfaces.PasswordRepository
import javax.inject.Inject

class GetTotalPasswordCountUseCase @Inject constructor(
    private val repository: PasswordRepository,
) {
     operator fun invoke() = repository.getTotalCount()
}