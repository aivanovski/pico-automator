package com.github.aivanovski.picoautomator.domain.steps.assertions

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode

interface Assertion {
    fun describe(): String
    fun assert(
        uiRoot: UiTreeNode,
        elements: List<ElementReference>
    ): Either<Exception, Unit>
}