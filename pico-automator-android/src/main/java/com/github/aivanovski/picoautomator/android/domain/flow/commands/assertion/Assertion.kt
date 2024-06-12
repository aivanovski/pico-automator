package com.github.aivanovski.picoautomator.android.domain.flow.commands.assertion

import com.github.aivanovski.picoautomator.android.entity.UiNode
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.AssertionException

interface Assertion {

    fun describe(): String
    fun assert(
        uiRoot: UiNode<*>,
        elements: List<UiElementSelector>
    ): Either<AssertionException, Unit>
}