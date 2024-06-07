package com.github.aivanovski.picoautomator.webapi

import kotlinx.serialization.Serializable

@Serializable
data class FlowsItemDto(
    val uid: String,
    val projectUid: String,
    val name: String
)