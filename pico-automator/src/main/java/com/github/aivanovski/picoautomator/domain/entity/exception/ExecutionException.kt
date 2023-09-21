package com.github.aivanovski.picoautomator.domain.entity.exception

open class ExecutionException(
    override val message: String? = null
) : Exception(message)