package com.roaa.data.mapper

import com.roaa.data.local.entity.PasswordEntity
import com.roaa.domain.model.Credentials

/**
 * Converts a Room entity to its domain model.
 * Used when reading from the database.
 */
fun PasswordEntity.toDomain(): Credentials = Credentials(
    id = id,
    serviceName = serviceName,
    domainName = domainName,
    logoUrl = logoUrl,
    username = username,
    password = password,
    websiteUrl = websiteUrl,
    notes = notes,
    strength = strength,
    createdAt = createdAt,
    updatedAt = updatedAt,
    serviceType = serviceType
)

/**
 * Converts a domain model to its Room entity.
 * Used when writing to the database.
 */
fun Credentials.toEntity(): PasswordEntity = PasswordEntity(
    id = id,
    serviceName = serviceName,
    domainName = domainName,
    username = username,
    logoUrl = logoUrl,
    password = password,
    websiteUrl = websiteUrl,
    notes = notes,
    strength = strength,
    createdAt = createdAt,
    updatedAt = updatedAt,
    serviceType = serviceType
)

/**
 * Converts a list of entities to a list of domain models.
 * Convenience extension for Flow<List<PasswordEntity>>.map { it.toDomainList() }
 */
fun List<PasswordEntity>.toDomainList(): List<Credentials> = map { it.toDomain() }