package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.GetUiTreeCommand
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.steps.assertions.Assertion
import com.github.aivanovski.picoautomator.extensions.findNode
import com.github.aivanovski.picoautomator.extensions.matches
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

internal class Assert(
    private val parentElement: ElementReference?,
    private val elements: List<ElementReference>,
    private val assertion: Assertion
) : ExecutableStepCommand<Unit>, FlakyFlowStep {

    override fun describe(): String {
        return when {
            parentElement != null -> {
                String.format(
                    "Assert %s: inside [%s] -> %s",
                    assertion.describe(),
                    parentElement.toReadableFormat(),
                    elements.toReadableFormat()
                )
            }

            else -> "Assert ${assertion.describe()}: ${elements.toReadableFormat()}"
        }
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        val getUiTreeResult = adbExecutor.execute(GetUiTreeCommand())
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.toLeft()
        }

        val rootNote = getUiTreeResult.unwrap()

        val nodeToLookup = if (parentElement == null) {
            rootNote
        } else {
            val parentNode = rootNote.findNode { node -> node.matches(parentElement) }
                ?: return Either.Left(Exception("Unable to find parent element: $parentElement"))

            parentNode
        }

        return assertion.assert(nodeToLookup, elements)
    }
}