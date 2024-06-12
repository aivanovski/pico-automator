package com.github.aivanovski.picoautomator.domain.newapi.entity.exception

open class FlowExecutionException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)