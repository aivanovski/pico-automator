package com.github.aivanovski.picoautomator.domain.entity.exception

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.steps.ExecutableStepCommand

internal class FailedStepException(
    val step: ExecutableStepCommand<Any>,
    val result: Either<Exception, Any>
) : ExecutionException()