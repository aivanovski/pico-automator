package com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.assertion

import com.github.aivanovski.picoautomator.android.ui.domain.entity.UiNode
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode

interface Assertion {

    fun describe(): String
    fun assert(
        uiRoot: UiNode<*>,
        elements: List<ElementReference>
    ): Either<Exception, Unit>
}