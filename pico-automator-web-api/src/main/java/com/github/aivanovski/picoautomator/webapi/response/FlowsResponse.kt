package com.github.aivanovski.picoautomator.webapi.response

import com.github.aivanovski.picoautomator.webapi.FlowsItemDto
import kotlinx.serialization.Serializable

@Serializable
class FlowsResponse(
    val flows: List<FlowsItemDto>
)