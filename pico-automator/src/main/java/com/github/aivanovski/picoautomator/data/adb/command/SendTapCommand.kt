package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Either

class SendTapCommand(
    private val x: Int,
    private val y: Int
) : AdbCommand<Unit> {

    override fun execute(environment: AdbEnvironment): Either<Exception, Unit> {
        return environment.run("shell input tap $x $y")
            .mapWith(Unit)
    }
}