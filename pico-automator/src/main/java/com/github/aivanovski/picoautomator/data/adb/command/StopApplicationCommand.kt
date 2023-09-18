package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Either

class StopApplicationCommand(
    private val packageName: String
) : AdbCommand<Unit> {

    override fun execute(environment: AdbEnvironment): Either<Exception, Unit> {
        return environment.run("shell am force-stop $packageName")
            .mapWith(Unit)
    }
}