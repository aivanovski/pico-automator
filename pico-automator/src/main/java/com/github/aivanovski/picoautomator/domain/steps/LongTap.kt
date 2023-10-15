package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.GetUiTreeCommand
import com.github.aivanovski.picoautomator.data.adb.command.SendSwipeCommand
import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.extensions.findNode
import com.github.aivanovski.picoautomator.extensions.matches
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

internal class LongTap(
    private val element: ElementReference
) : ExecutableFlowStep<Unit>, FlakyFlowStep {

    override fun describe(): String = "Long tap on element: ${element.toReadableFormat()}"

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        val getUiTreeResult = adbExecutor.execute(GetUiTreeCommand())
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.mapToLeft()
        }

        val rootNode = getUiTreeResult.unwrap()

        val node = rootNode.findNode { node -> node.matches(element) }
            ?: return Either.Left(Exception("Unable to find node with: $element"))

        if (node.bounds == null) {
            return Either.Left(Exception("Unable to find bounds for node: $node"))
        }

        val x = node.bounds.centerX()
        val y = node.bounds.centerY()

        return adbExecutor.execute(
            SendSwipeCommand(
                startX = x,
                startY = y,
                endX = x,
                endY = y,
                duration = Duration.millis(1000)
            )
        )
    }
}