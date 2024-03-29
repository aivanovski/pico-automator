package com.github.aivanovski.picoautomator

import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode

interface PicoAutomatorApi {
    fun launch(packageName: String, isClearState: Boolean = false): Either<Exception, Unit>
    fun assertVisible(element: ElementReference): Either<Exception, Unit>
    fun assertVisible(elements: List<ElementReference>): Either<Exception, Unit>
    fun assertNotVisible(element: ElementReference): Either<Exception, Unit>
    fun assertNotVisible(elements: List<ElementReference>): Either<Exception, Unit>
    fun tapOn(element: ElementReference): Either<Exception, Unit>
    fun longTapOn(element: ElementReference): Either<Exception, Unit>
    fun inputText(text: String): Either<Exception, Unit>
    fun inputText(element: ElementReference, text: String): Either<Exception, Unit>
    fun pressBack(): Either<Exception, Unit>
    fun pressKey(keyCode: String): Either<Exception, Unit>
    fun isVisible(element: ElementReference): Boolean
    fun getUiTree(): UiTreeNode

    fun waitUntil(
        element: ElementReference,
        timeout: Duration
    ): Either<Exception, Unit>

    fun waitUntil(
        element: ElementReference,
        timeout: Duration,
        step: Duration
    ): Either<Exception, Unit>

    fun sleep(duration: Duration): Either<Exception, Unit>
    fun fail(message: String)
    fun complete(message: String)
    fun shell(command: String)
}