package com.roaa.presentation.utils.models

data class PasswordStats(
    val total: Int,
    val safe: Int,
    val reused: Int,
    val compromised: Int,
    val weak: Int
)