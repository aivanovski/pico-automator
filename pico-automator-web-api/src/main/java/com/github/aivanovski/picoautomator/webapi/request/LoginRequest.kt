package com.github.aivanovski.picoautomator.webapi.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)