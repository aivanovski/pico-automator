package com.github.aivanovski.picoautomator.android.ui.domain.flow.steps

import com.github.aivanovski.picoautomator.android.ui.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.android.ui.extensions.findNode
import com.github.aivanovski.picoautomator.android.ui.extensions.matches
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class Tap(
    private val element: ElementReference
) : ExecutableFlowStep<Unit> {

    override fun describe(): String = "Tap on element: ${element.toReadableFormat()}"

    override fun execute(driver: Driver): Either<Exception, Unit> {
        val getUiTreeResult = driver.getUiTree()
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.mapToLeft()
        }

        val rootNode = getUiTreeResult.unwrap()

        val node = rootNode.findNode { node -> node.matches(element) }
            ?: return Either.Left(Exception("Unable to find node with: $element"))

        return driver.tapOn(node)
    }
}