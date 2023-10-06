package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.SendKeyEventCommand
import com.github.aivanovski.picoautomator.domain.entity.Either

internal class PressBack : ExecutableFlowStep<Unit> {

    override fun describe(): String = "Press back"

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        return adbExecutor.execute(SendKeyEventCommand("KEYCODE_BACK"))
    }
}