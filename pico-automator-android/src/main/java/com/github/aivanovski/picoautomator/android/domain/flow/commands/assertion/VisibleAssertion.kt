package com.github.aivanovski.picoautomator.android.domain.flow.commands.assertion

import com.github.aivanovski.picoautomator.android.entity.UiNode
import com.github.aivanovski.picoautomator.android.extensions.hasElement
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.AssertionException
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class VisibleAssertion : Assertion {

    override fun describe(): String = "is visible"

    override fun assert(
        uiRoot: UiNode<*>,
        elements: List<UiElementSelector>
    ): Either<AssertionException, Unit> {
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