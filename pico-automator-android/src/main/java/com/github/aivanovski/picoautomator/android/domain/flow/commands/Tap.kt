package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.android.extensions.findNode
import com.github.aivanovski.picoautomator.android.extensions.getNodeParents
import com.github.aivanovski.picoautomator.android.extensions.matches
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FailedToFindNodeException
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class Tap(
    private val element: UiElementSelector,
    private val isLongTap: Boolean
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "%s on element: %s".format(
            if (isLongTap) "Long Tap" else "Tap",
            element.toReadableFormat()
        )
    }

    override suspend fun execute(driver: Driver): Either<FlowExecutionException, Unit> {
        val getUiTreeResult = driver.getUiTree()
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.toLeft()
        }

        val rootNode = getUiTreeResult.unwrap()

        val node = rootNode.findNode { node -> node.matches(element) }
            ?: return Either.Left(FailedToFindNodeException(element))

        val nodeSelector = getSelectorForNode()

        val tappableNode = if (!node.matches(nodeSelector)) {
            val parents = rootNode.getNodeParents(node)

            val clickableParent = parents.lastOrNull { parent -> parent.matches(nodeSelector) }
                ?: return Either.Left(FailedToFindNodeException(nodeSelector))

            clickableParent
        } else {
            node
        }

        return if (isLongTap) {
            driver.longTapOn(tappableNode)
        } else {
            driver.tapOn(tappableNode)
        }
    }

    private fun getSelectorForNode(): UiElementSelector {
        return if (isLongTap) {
            LONG_CLICKABLE_ELEMENT
        } else {
            CLICKABLE_ELEMENT
        }
    }

    companion object {
        private val CLICKABLE_ELEMENT = UiElementSelector.isClickable(true)
        private val LONG_CLICKABLE_ELEMENT = UiElementSelector.isLongClickable(true)
    }
}