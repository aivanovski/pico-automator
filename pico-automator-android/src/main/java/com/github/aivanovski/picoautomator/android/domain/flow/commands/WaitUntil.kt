package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.android.extensions.hasElement
import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Either.Companion.left
import com.github.aivanovski.picoautomator.domain.entity.Either.Companion.right
import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FailedToFindNodeException
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException
import com.github.aivanovski.picoautomator.extensions.toMilliseconds
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

class WaitUntil(
    private val element: UiElementSelector,
    private val step: Duration,
    private val timeout: Duration
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return String.format(
            "Wait for element: %s, timeout = %s, step = %s",
            element.toReadableFormat(),
            timeout.toReadableFormat(),
            step.toReadableFormat()
        )
    }

    override suspend fun execute(driver: Driver): Either<FlowExecutionException, Unit> {
        val startTime = System.currentTimeMillis()

        while ((System.currentTimeMillis() - startTime) <= timeout.toMilliseconds()) {
            val sleepResult = Sleep(step).execute(driver)
            if (sleepResult.isLeft()) {
                return sleepResult.toLeft()
            }

            val getUiTreeResult = driver.getUiTree()
            if (getUiTreeResult.isLeft()) {
                return getUiTreeResult.toLeft()
            }

            val rootNode = getUiTreeResult.unwrap()
            if (rootNode.hasElement(element)) {
                return right(Unit)
            }
        }

        return left(FailedToFindNodeException(element))
    }
}