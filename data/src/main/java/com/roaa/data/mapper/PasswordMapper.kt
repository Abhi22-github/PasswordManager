package com.roaa.data.mapper

import com.roaa.data.local.PasswordEncryptor
import com.roaa.data.local.entity.PasswordEntity
import com.roaa.domain.model.Credentials

fun PasswordEntity.toDomain(encryptor: PasswordEncryptor): Credentials = Credentials(
    id = id,
    serviceName = serviceName,
    domainName = domainName,
    logoUrl = logoUrl,
    username = username,
    password = encryptor.decrypt(password),
    websiteUrl = websiteUrl,
    notes = notes,
    strength = strength,
    createdAt = createdAt,
    updatedAt = updatedAt,
    serviceType = serviceType,
    lastCopiedAt = lastCopiedAt
)

fun Credentials.toEntity(encryptor: PasswordEncryptor): PasswordEntity = PasswordEntity(
    id = id,
    serviceName = serviceName,
    domainName = domainName,
    username = username,
    logoUrl = logoUrl,
    password = encryptor.encrypt(password),
    websiteUrl = websiteUrl,
    notes = notes,
    strength = strength,
    createdAt = createdAt,
    updatedAt = updatedAt,
    serviceType = serviceType,
    lastCopiedAt = lastCopiedAt
)

fun List<PasswordEntity>.toDomainList(encryptor: PasswordEncryptor): List<Credentials> =
    map { it.toDomain(encryptor) }
