package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.GetUiTreeCommand
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.extensions.findNode
import com.github.aivanovski.picoautomator.extensions.matches
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

internal class AssertVisible(
    private val parentElement: ElementReference?,
    private val elements: List<ElementReference>
) : ExecutableFlowStep<Unit>, FlakyFlowStep {

    override fun describe(): String {
        val formattedElements = if (elements.size == 1) {
            elements.first().toReadableFormat()
        } else {
            elements.toReadableFormat()
        }

        return when {
            parentElement != null -> {
                String.format(
                    "Assert is visible: inside [%s] -> %s",
                    parentElement.toReadableFormat(),
                    formattedElements
                )
            }

            else -> "Assert is visible: $formattedElements"
        }
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        val getUiTreeResult = adbExecutor.execute(GetUiTreeCommand())
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.mapToLeft()
        }

        val rootNote = getUiTreeResult.unwrap()

        val nodeToLookup = if (parentElement == null) {
            rootNote
        } else {
            val parentNode = rootNote.findNode { node -> node.matches(parentElement) }
                ?: return Either.Left(Exception("Unable to find parent element: $parentElement"))

            parentNode
        }

        for (element in elements) {
            val node = nodeToLookup.findNode { node -> node.matches(element) }
            if (node == null) {
                return Either.Left(Exception("Unable to find element: $element"))
            }
        }

        return Either.Right(Unit)
    }
}