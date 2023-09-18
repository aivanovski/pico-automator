package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Either

class SendTextCommand(
    private val text: String
) : AdbCommand<Unit> {

    override fun execute(environment: AdbEnvironment): Either<Exception, Unit> {
        return environment.run("shell input text \"$text\"")
            .mapWith(Unit)
    }
}