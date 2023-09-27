package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.StartApplicationCommand
import com.github.aivanovski.picoautomator.data.adb.command.StopApplicationCommand
import com.github.aivanovski.picoautomator.domain.entity.Either

internal class Launch(
    private val packageName: String
) : ExecutableFlowStep<Unit> {

    override fun describe(): String {
        return "Launch app: $packageName"
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        adbExecutor.execute(StopApplicationCommand(packageName))
        return adbExecutor.execute(StartApplicationCommand(packageName))
    }
}