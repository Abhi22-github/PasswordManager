package com.roaa.data.repository

import com.roaa.data.local.dao.PasswordDao
import com.roaa.data.mapper.toDomain
import com.roaa.data.mapper.toDomainList
import com.roaa.data.mapper.toEntity
import com.roaa.domain.model.Credentials
import com.roaa.domain.repositoryInterfaces.PasswordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PasswordRepositoryImpl @Inject constructor(
    private val dao: PasswordDao
) : PasswordRepository {

    override fun getAllPasswords(): Flow<List<Credentials>> =
        dao.getAll().map { it.toDomainList() }

    override fun search(query: String): Flow<List<Credentials>> =
        dao.search(query).map { it.toDomainList() }


    override fun getTotalCount(): Flow<Int> = dao.getCount()

    override fun getWeakPasswordCount(): Flow<Int> = dao.getWeakPasswordCount()
    override fun getReusedPasswordCount(): Flow<Int> = dao.getReusedPasswordGroupCount()

    override suspend fun getById(id: String): Credentials? =
        dao.getById(id)?.toDomain()

    override suspend fun addPassword(credentials: Credentials) {
        dao.insert(credentials.toEntity())
    }

    override fun observePasswordById(id: String): Flow<Credentials?> {
        return dao.observePasswordById(id).map { it?.toDomain() }
    }

    override suspend fun updatePassword(credentials: Credentials) {
        val updated = credentials.copy(updatedAt = System.currentTimeMillis())
        dao.update(updated.toEntity())
    }

    override suspend fun deletePassword(credentials: Credentials) {
        dao.delete(credentials.toEntity())
    }

    override suspend fun deleteById(id: String) {
        dao.deleteById(id)
    }
}