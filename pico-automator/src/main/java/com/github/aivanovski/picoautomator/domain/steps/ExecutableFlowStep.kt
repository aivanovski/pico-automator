package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.domain.entity.Either

internal interface ExecutableFlowStep<out T : Any> : FlowStep {
    fun execute(adbExecutor: AdbExecutor): Either<Exception, T>
}