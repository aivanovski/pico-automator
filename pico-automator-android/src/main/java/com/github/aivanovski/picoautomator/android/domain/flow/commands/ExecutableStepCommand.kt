package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException
import com.github.aivanovski.picoautomator.domain.steps.StepCommand

interface ExecutableStepCommand<out T : Any> : StepCommand {
    suspend fun execute(driver: Driver): Either<FlowExecutionException, T>
}