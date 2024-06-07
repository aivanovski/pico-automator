package com.github.aivanovski.picoautomator.webapi.response

import kotlinx.serialization.Serializable

@Serializable
data class ProjectsItemDto(
    val uid: String,
    val name: String
)