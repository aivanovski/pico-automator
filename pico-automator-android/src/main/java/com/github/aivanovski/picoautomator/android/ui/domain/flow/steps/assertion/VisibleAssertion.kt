package com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.assertion

import com.github.aivanovski.picoautomator.android.ui.domain.entity.UiNode
import com.github.aivanovski.picoautomator.android.ui.extensions.hasElement
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.exception.AssertionException
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class VisibleAssertion : Assertion {

    override fun describe(): String = "is visible"

    override fun assert(
        uiRoot: UiNode<*>,
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