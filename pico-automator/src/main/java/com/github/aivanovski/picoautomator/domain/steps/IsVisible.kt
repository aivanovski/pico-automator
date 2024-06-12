package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.extensions.findNode
import com.github.aivanovski.picoautomator.extensions.hasElement
import com.github.aivanovski.picoautomator.extensions.matches
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

internal class IsVisible(
    private val parentElement: ElementReference?,
    private val elements: List<ElementReference>
) : ExecutableStepCommand<Boolean>, FlakyFlowStep {

    override fun describe(): String {
        return when {
            parentElement != null -> {
                String.format(
                    "Is visible: inside [%s] -> %s",
                    parentElement.toReadableFormat(),
                    elements.toReadableFormat()
                )
            }

            else -> "Is visible: ${elements.toReadableFormat()}"
        }
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Boolean> {
        val getUiTreeResult = GetUiTree().execute(adbExecutor)
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.toLeft()
        }

        val rootNode = getUiTreeResult.unwrap()
        val nodeToLookup = if (parentElement == null) {
            rootNode
        } else {
            val parentNode = rootNode.findNode { node -> node.matches(parentElement) }
                ?: return Either.Left(Exception("Unable to find parent element: $parentElement"))

            parentNode
        }

        val isAllVisible = elements.all { element -> nodeToLookup.hasElement(element) }

        return Either.Right(isAllVisible)
    }
}