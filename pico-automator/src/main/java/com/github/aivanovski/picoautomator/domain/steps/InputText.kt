package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.SendTextCommand
import com.github.aivanovski.picoautomator.domain.entity.Either

class InputText(
    private val text: String
) : FlowStep {

    override fun describe(): String {
        return "Input text: $text"
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        return adbExecutor.execute(SendTextCommand(text))
    }
}