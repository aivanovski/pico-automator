package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.GetUiDumpCommand
import com.github.aivanovski.picoautomator.data.adb.command.SendTapCommand
import com.github.aivanovski.picoautomator.extensions.findNode
import com.github.aivanovski.picoautomator.extensions.matches
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference

class Tap(
    private val element: ElementReference
) : FlowStep, FlakyFlowStep {

    override fun describe(): String {
        return "Tap on element: $element"
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        val getDumpResult = adbExecutor.execute(GetUiDumpCommand())
        if (getDumpResult.isLeft()) {
            return getDumpResult.mapToLeft()
        }

        val rootNode = getDumpResult.unwrap()

        val node = rootNode.findNode { node -> node.matches(element) }
            ?: return Either.Left(Exception("Unable to find node with: $element"))

        if (node.bounds == null) {
            return Either.Left(Exception("Unable to find bounds for node: $node"))
        }

        val x = node.bounds.centerX()
        val y = node.bounds.centerY()

        return adbExecutor.execute(SendTapCommand(x, y))
    }
}