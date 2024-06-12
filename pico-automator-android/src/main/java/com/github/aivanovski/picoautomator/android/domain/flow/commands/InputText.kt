package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.android.extensions.findNode
import com.github.aivanovski.picoautomator.android.extensions.matches
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FailedToFindNodeException
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class InputText(
    private val text: String,
    private val element: UiElementSelector? = null
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return if (element != null) {
            "Input text: [%s] into %s".format(text, element.toReadableFormat())
        } else {
            "Input text: [%s]".format(text)
        }
    }

    override suspend fun execute(driver: Driver): Either<FlowExecutionException, Unit> {
        val getUiTreeResult = driver.getUiTree()
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.toLeft()
        }

        val uiRoot = getUiTreeResult.unwrap()
        val selector = element ?: FOCUSED_ELEMENT

        val targetNode = uiRoot.findNode { node -> node.matches(selector) }
            ?: return Either.Left(FailedToFindNodeException(selector))

        return driver.inputText(text, targetNode)
    }

    companion object {
        private val FOCUSED_ELEMENT = UiElementSelector.isFocused(true)
    }
}