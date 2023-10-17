package com.github.aivanovski.picoautomator.domain.steps.assertions

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode
import com.github.aivanovski.picoautomator.domain.entity.exception.AssertionException
import com.github.aivanovski.picoautomator.extensions.hasElement
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class VisibleAssertion : Assertion {

    override fun describe(): String = "is visible"

    override fun assert(
        uiRoot: UiTreeNode,
        elements: List<ElementReference>
    ): Either<Exception, Unit> {
        val unresolvedElements = elements.filter { element -> !uiRoot.hasElement(element) }

        return if (unresolvedElements.isEmpty()) {
            Either.Right(Unit)
        } else {
            Either.Left(
                AssertionException(
                    String.format(
                        "Elements should be visible: %s",
                        unresolvedElements.toReadableFormat()
                    )
                )
            )
        }
    }
}