package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.GetUiTreeCommand
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.extensions.findNode
import com.github.aivanovski.picoautomator.extensions.matches

internal class AssertVisible(
    private val parentElement: ElementReference?,
    private val elements: List<ElementReference>
) : ExecutableFlowStep<Unit>, FlakyFlowStep {

    override fun describe(): String {
        return when {
            parentElement != null -> {
                "Assert is visible: inside [$parentElement] -> $elements"
            }

            else -> "Assert is visible: $elements"
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