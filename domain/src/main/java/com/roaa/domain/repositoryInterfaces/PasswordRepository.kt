package com.roaa.domain.repositoryInterfaces

import com.roaa.domain.model.Credentials
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {
    fun getAllPasswords(): Flow<List<Credentials>>

    fun search(query: String): Flow<List<Credentials>>


    fun getTotalCount(): Flow<Int>

    fun getWeakPasswordCount(): Flow<Int>

    fun getReusedPasswordCount(): Flow<Int>

    suspend fun getById(id: String): Credentials?

    suspend fun addPassword(credentials: Credentials)

    fun observePasswordById(id: String) : Flow<Credentials?>

    suspend fun updatePassword(credentials: Credentials)

    suspend fun deletePassword(credentials: Credentials)

    suspend fun deleteById(id: String)

    suspend fun recordPasswordCopied(id: String)

    fun getRecentlyCopied(): Flow<List<Credentials>>
}