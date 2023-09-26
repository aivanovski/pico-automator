package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Either

internal class StopApplicationCommand(
    private val packageName: String
) : AdbCommand<Unit> {

    override fun execute(environment: AdbEnvironment): Either<Exception, Unit> {
        return environment.run(String.format(COMMAND, packageName))
            .mapWith(Unit)
    }

    companion object {
        internal const val COMMAND = "shell am force-stop %s"
    }
}