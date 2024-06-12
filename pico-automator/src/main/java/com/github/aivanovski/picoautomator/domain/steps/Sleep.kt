package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.extensions.toMilliseconds
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

internal class Sleep(
    private val duration: Duration
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "Sleep ${duration.toReadableFormat()}"
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        Thread.sleep(duration.toMilliseconds()) // TODO: coroutines can be used instead
        return Either.Right(Unit)
    }
}