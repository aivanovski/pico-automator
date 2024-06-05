package com.github.aivanovski.picoautomator.android.ui.domain.flow.steps

import com.github.aivanovski.picoautomator.android.ui.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.assertion.Assertion
import com.github.aivanovski.picoautomator.android.ui.extensions.findNode
import com.github.aivanovski.picoautomator.android.ui.extensions.matches
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.steps.FlakyFlowStep
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class Assert(
    private val parentElement: ElementReference?,
    private val elements: List<ElementReference>,
    private val assertion: Assertion
) : ExecutableFlowStep<Unit>, FlakyFlowStep {

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

    override fun execute(driver: Driver): Either<Exception, Unit> {
        val getUiTreeResult = driver.getUiTree()
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

        return assertion.assert(nodeToLookup, elements)
    }
}