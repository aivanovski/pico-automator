package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Either

internal class SendKeyEventCommand(
    private val keyCode: String
) : AdbCommand<Unit> {

    override fun execute(environment: AdbEnvironment): Either<Exception, Unit> {
        return environment.run("shell input keyevent \"$keyCode\"")
            .mapWith(Unit)
    }
}