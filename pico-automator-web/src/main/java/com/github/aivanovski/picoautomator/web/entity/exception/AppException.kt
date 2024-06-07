package com.github.aivanovski.picoautomator.web.entity.exception

open class AppException(
    cause: Exception?,
    message: String?
) : Exception(message, cause) {

    constructor(message: String) : this(null, message)
    constructor(cause: Exception) : this(cause, null)
}