package com.github.aivanovski.picoautomator.webapi

import kotlinx.serialization.Serializable

@Serializable
data class FlowItemDto(
    val uid: String,
    val projectUid: String,
    val name: String,
    val base64Content: String
)