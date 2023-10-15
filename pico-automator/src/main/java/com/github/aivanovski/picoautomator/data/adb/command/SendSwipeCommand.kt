package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Either

internal class SendSwipeCommand(
    private val startX: Int,
    private val startY: Int,
    private val endX: Int,
    private val endY: Int,
    private val duration: Duration
) : AdbCommand<Unit> {

    override fun execute(environment: AdbEnvironment): Either<Exception, Unit> {
        val durationInMillis = duration.milliseconds

        return environment.run("shell input swipe $startX $startY $endX $endY $durationInMillis")
            .mapWith(Unit)
    }
}