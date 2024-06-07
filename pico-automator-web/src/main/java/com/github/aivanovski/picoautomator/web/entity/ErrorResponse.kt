package com.github.aivanovski.picoautomator.web.entity

import com.github.aivanovski.picoautomator.web.entity.exception.AppException
import io.ktor.http.HttpStatusCode

data class ErrorResponse(
    val status: HttpStatusCode,
    val exception: AppException,
    val message: String?
) {

    companion object {

        fun fromException(
            status: HttpStatusCode,
            exception: AppException
        ): ErrorResponse =
            ErrorResponse(
                status = status,
                exception = exception,
                message = exception.message
            )
    }
}