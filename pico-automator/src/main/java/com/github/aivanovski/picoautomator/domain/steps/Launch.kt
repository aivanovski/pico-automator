package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.ClearApplicationDataCommand
import com.github.aivanovski.picoautomator.data.adb.command.StartApplicationCommand
import com.github.aivanovski.picoautomator.data.adb.command.StopApplicationCommand
import com.github.aivanovski.picoautomator.domain.entity.Either

internal class Launch(
    private val packageName: String,
    private val isClearState: Boolean
) : ExecutableFlowStep<Unit> {

    override fun describe(): String {
        return StringBuilder().apply {
            append("Launch app: package name = $packageName")

            if (isClearState) {
                append(", clear state = true")
            }
        }
            .toString()
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        if (isClearState) {
            val clearResult = adbExecutor.execute(ClearApplicationDataCommand(packageName))
            if (clearResult.isLeft()) {
                return clearResult
            }
        } else {
            val stopResult = adbExecutor.execute(StopApplicationCommand(packageName))
            if (stopResult.isLeft()) {
                return stopResult
            }
        }

        return adbExecutor.execute(StartApplicationCommand(packageName))
    }
}