package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException
import com.github.aivanovski.picoautomator.extensions.toMilliseconds
import com.github.aivanovski.picoautomator.extensions.toReadableFormat
import kotlinx.coroutines.delay

class Sleep(
    private val duration: Duration
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "Sleep %s".format(duration.toReadableFormat())
    }

    override suspend fun execute(driver: Driver): Either<FlowExecutionException, Unit> {
        delay(duration.toMilliseconds())
        return Either.Right(Unit)
    }
}