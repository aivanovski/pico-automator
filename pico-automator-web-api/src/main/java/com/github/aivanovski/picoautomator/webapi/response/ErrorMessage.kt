package com.github.aivanovski.picoautomator.webapi.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val message: String
)