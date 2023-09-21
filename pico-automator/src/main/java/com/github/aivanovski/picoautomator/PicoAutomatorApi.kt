package com.github.aivanovski.picoautomator

import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode

interface PicoAutomatorApi {
    fun launch(packageName: String): Either<Exception, Unit>
    fun assertVisible(element: ElementReference): Either<Exception, Unit>
    fun tapOn(element: ElementReference): Either<Exception, Unit>
    fun inputText(text: String): Either<Exception, Unit>
    fun isVisible(element: ElementReference): Boolean
    fun getUiTree(): UiTreeNode

    fun waitFor(
        element: ElementReference,
        timeout: Duration
    ): Either<Exception, Unit>

    fun waitFor(
        element: ElementReference,
        timeout: Duration,
        step: Duration
    ): Either<Exception, Unit>

    fun wait(duration: Duration): Either<Exception, Unit>
    fun fail(message: String)
    fun complete(message: String)
}