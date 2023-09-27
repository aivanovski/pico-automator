package com.github.aivanovski.picoautomator.domain.entity.exception

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.steps.ExecutableFlowStep

internal class FailedStepException(
    val step: ExecutableFlowStep<Any>,
    val result: Either<Exception, Any>
) : ExecutionException()