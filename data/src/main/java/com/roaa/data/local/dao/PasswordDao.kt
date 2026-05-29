package com.roaa.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.roaa.data.local.entity.PasswordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {

    @Query("SELECT * FROM passwords ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<PasswordEntity>>

    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getById(id: String): PasswordEntity?

    @Query("SELECT * FROM passwords WHERE serviceName LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<PasswordEntity>>

    @Query("SELECT COUNT(*) FROM passwords")
    fun getCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM passwords WHERE strength < 0.5")
    fun getWeakPasswordCount(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT password FROM passwords
            GROUP BY password
            HAVING COUNT(*) > 1
        )
    """)
    fun getReusedPasswordGroupCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(password: PasswordEntity)

    @Update
    suspend fun update(password: PasswordEntity)

    @Query("SELECT * FROM passwords WHERE id = :id")
    fun observePasswordById(id: String): Flow<PasswordEntity?>

    @Delete
    suspend fun delete(password: PasswordEntity)

    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM passwords")
    suspend fun deleteAll()

    @Query("UPDATE passwords SET lastCopiedAt = :timestamp WHERE id = :id")
    suspend fun updateLastCopiedAt(id: String, timestamp: Long)

    @Query("SELECT * FROM passwords WHERE lastCopiedAt IS NOT NULL ORDER BY lastCopiedAt DESC LIMIT 10")
    fun getRecentlyCopied(): Flow<List<PasswordEntity>>
}