package com.github.aivanovski.picoautomator.android.ui.domain.flow.steps

import com.github.aivanovski.picoautomator.android.ui.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.android.ui.extensions.findNode
import com.github.aivanovski.picoautomator.android.ui.extensions.matches
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class InputText(
    private val text: String,
    private val element: ElementReference? = null
) : ExecutableFlowStep<Unit> {

    override fun describe(): String {
        return if (element != null) {
            "Input text: [$text] into ${element.toReadableFormat()}"
        } else {
            "Input text: [$text]"
        }
    }

    override fun execute(driver: Driver): Either<Exception, Unit> {
        if (element == null) {
            return driver.inputText(text)
        }

        val getUiTreeResult = driver.getUiTree()
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.mapToLeft()
        }

        val uiRoot = getUiTreeResult.unwrap()
        val targetNode = uiRoot.findNode { node -> node.matches(element) }
            ?: return Either.Left(Exception("Unable to find node: ${element.toReadableFormat()}"))

        if (targetNode.entity.isFocused != true) {
            return Either.Left(Exception("Element ${element.toReadableFormat()} is not focused"))
        }

        return driver.inputText(text)
    }
}