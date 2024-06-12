package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.process.ProcessExecutor
import com.github.aivanovski.picoautomator.domain.entity.Either

internal class Shell(
    private val processExecutor: ProcessExecutor,
    private val command: String
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "Shell: $command"
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        return processExecutor.run(command)
            .mapWith(Unit)
    }
}