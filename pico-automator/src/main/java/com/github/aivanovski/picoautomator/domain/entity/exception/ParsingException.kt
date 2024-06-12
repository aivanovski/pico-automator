package com.github.aivanovski.picoautomator.domain.entity.exception

class ParsingException(cause: Exception?, message: String?) : Exception(message, cause) {
    constructor(cause: Exception) : this(cause, null)
    constructor(message: String) : this(null, message)
}
