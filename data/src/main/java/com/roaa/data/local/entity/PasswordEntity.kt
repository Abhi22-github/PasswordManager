package com.roaa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.roaa.domain.model.ServiceType
import java.util.UUID

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val serviceName: String,
    val domainName: String,
    val logoUrl: String? = null,
    val username: String,
    val password: String,
    val websiteUrl: String? = null,
    val notes: String? = null,
    val serviceType: ServiceType,
    val strength: Float = 0f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastCopiedAt: Long? = null
)
