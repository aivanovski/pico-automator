package com.github.aivanovski.picoautomator.data.adb

import com.github.aivanovski.picoautomator.data.adb.command.AdbCommand
import com.github.aivanovski.picoautomator.domain.entity.Either

class AdbExecutor(
    private val environment: AdbEnvironment
) {

    fun <T> execute(command: AdbCommand<T>): Either<Exception, T> {
        return command.execute(environment)
    }

    fun cloneWithEnvironment(newEnvironment: AdbEnvironment): AdbExecutor {
        return AdbExecutor(newEnvironment)
    }
}