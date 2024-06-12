package com.github.aivanovski.picoautomator.android.data.api.entity

import io.ktor.http.HttpStatusCode

open class ApiException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)

class InvalidHttpStatusCodeException(
    status: HttpStatusCode
) : ApiException(message = "Invalid HTTP status code: %s".format(status))