package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.domain.entity.Either

internal interface ExecutableStepCommand<out T : Any> : StepCommand {
    fun execute(adbExecutor: AdbExecutor): Either<Exception, T>
}