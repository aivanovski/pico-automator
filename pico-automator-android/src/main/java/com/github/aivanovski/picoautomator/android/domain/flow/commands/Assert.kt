package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.android.domain.flow.commands.assertion.Assertion
import com.github.aivanovski.picoautomator.android.extensions.findNode
import com.github.aivanovski.picoautomator.android.extensions.matches
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FailedToFindNodeException
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException
import com.github.aivanovski.picoautomator.domain.steps.FlakyFlowStep
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class Assert(
    private val parent: UiElementSelector?,
    private val elements: List<UiElementSelector>,
    private val assertion: Assertion
) : ExecutableStepCommand<Unit>, FlakyFlowStep {

    override fun describe(): String {
        return when {
            parent != null -> {
                String.format(
                    "Assert %s: inside [%s] -> %s",
                    assertion.describe(),
                    parent.toReadableFormat(),
                    elements.toReadableFormat()
                )
            }

            else -> "Assert %s: %s".format(assertion.describe(), elements.toReadableFormat())
        }
    }

    override suspend fun execute(driver: Driver): Either<FlowExecutionException, Unit> {
        val getUiTreeResult = driver.getUiTree()
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.toLeft()
        }

        val rootNote = getUiTreeResult.unwrap()

        val nodeToLookup = if (parent == null) {
            rootNote
        } else {
            val parentNode = rootNote.findNode { node -> node.matches(parent) }
                ?: return Either.Left(FailedToFindNodeException(parent))

            parentNode
        }

        return assertion.assert(nodeToLookup, elements)
    }
}