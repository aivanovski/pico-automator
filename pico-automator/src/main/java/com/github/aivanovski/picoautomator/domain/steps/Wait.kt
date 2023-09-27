package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

internal class Wait(
    private val duration: Duration
) : ExecutableFlowStep<Unit> {

    override fun describe(): String {
        return "Wait ${duration.toReadableFormat()}"
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        Thread.sleep(duration.milliseconds) // TODO: coroutines can be used instead
        return Either.Right(Unit)
    }
}