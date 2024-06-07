package com.github.aivanovski.picoautomator.webapi.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)