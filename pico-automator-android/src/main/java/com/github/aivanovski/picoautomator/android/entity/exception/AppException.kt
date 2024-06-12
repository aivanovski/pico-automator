package com.github.aivanovski.picoautomator.android.entity.exception

open class AppException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)