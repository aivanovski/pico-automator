package com.github.aivanovski.picoautomator.cli.domain

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.presentation.OutputWriter

class ErrorHandler(
    private val writer: OutputWriter
) {

    fun processIfLeft(result: Either<Exception, Any?>) {
        if (result.isLeft()) {
            val exception = result.unwrapError()
            writer.printStackTrace(exception)
        }
    }
}