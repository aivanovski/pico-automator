package com.github.aivanovski.picoautomator.web.extensions

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.web.entity.ErrorResponse
import com.github.aivanovski.picoautomator.web.entity.exception.AppException
import io.ktor.http.HttpStatusCode
import kotlin.Exception

fun Exception.toErrorResponse(): ErrorResponse {
    return ErrorResponse.fromException(
        status = HttpStatusCode.BadRequest, // TODO: could depend on exception
        exception = if (this is AppException) {
            this
        } else {
            AppException(this)
        }
    )
}

fun <Value> Either<Exception, Value>.toErrorResponse(): Either.Left<ErrorResponse> {
    return Either.Left(unwrapError().toErrorResponse())
}