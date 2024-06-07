package com.github.aivanovski.picoautomator.webapi.response

import com.github.aivanovski.picoautomator.webapi.FlowItemDto
import kotlinx.serialization.Serializable

@Serializable
data class FlowResponse(
    val flow: FlowItemDto
)