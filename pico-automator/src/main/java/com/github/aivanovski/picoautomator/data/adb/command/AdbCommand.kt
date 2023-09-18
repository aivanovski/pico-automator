package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Either

interface AdbCommand<Result> {

    fun execute(
        environment: AdbEnvironment
    ): Either<Exception, Result>
}