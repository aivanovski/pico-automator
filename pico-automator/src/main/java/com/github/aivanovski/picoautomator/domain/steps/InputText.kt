package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.GetUiTreeCommand
import com.github.aivanovski.picoautomator.data.adb.command.SendTextCommand
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.extensions.findNode
import com.github.aivanovski.picoautomator.extensions.matches
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

internal class InputText(
    private val text: String,
    private val element: ElementReference?
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return if (element != null) {
            "Input text: [$text] into ${element.toReadableFormat()}"
        } else {
            "Input text: [$text]"
        }
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        if (element == null) {
            return adbExecutor.execute(SendTextCommand(text))
        }

        val getUiTreeResult = adbExecutor.execute(GetUiTreeCommand())
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.toLeft()
        }

        val uiRoot = getUiTreeResult.unwrap()
        val node = uiRoot.findNode { node -> node.matches(element) }
        if (node?.isFocused != true) {
            return Either.Left(Exception("Element ${element.toReadableFormat()} is not focused"))
        }

        return adbExecutor.execute(SendTextCommand(text))
    }
}