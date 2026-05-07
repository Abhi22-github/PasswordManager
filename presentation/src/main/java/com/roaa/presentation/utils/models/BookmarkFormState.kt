package com.roaa.presentation.utils.models

import com.roaa.domain.model.Credentials
import com.roaa.domain.model.ServiceType

/**
 * The user-editable fields of a password record. Lives outside any single
 * screen because both the editor and (eventually) detail-edit dialogs use it.
 */
data class BookmarkFormState(
    val serviceName: String = "",
    val username: String = "",
    val domainName: String = "",
    val password: String = "",
    val notes: String? = "",
    val logoUrl: String = "",
    val serviceType: ServiceType = ServiceType.WEBSITE,
    val passwordStrength: Float = 0f,
) {
    val isValid: Boolean
        get() = serviceName.isNotBlank() &&
                username.isNotBlank() &&
                password.isNotBlank()

    fun matches(p: Credentials): Boolean =
        serviceName == p.serviceName &&
                domainName == p.domainName &&
                username == p.username &&
                password == p.password &&
                notes == p.notes

    companion object {
        fun fromPassword(p: Credentials): BookmarkFormState = BookmarkFormState(
            serviceName = p.serviceName,
            username = p.username,
            password = p.password,
            notes = p.notes,
            domainName = p.domainName,
            serviceType = p.serviceType,
            passwordStrength = p.strength
        )
    }
}