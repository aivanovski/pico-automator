package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.extensions.hasElement
import com.github.aivanovski.picoautomator.extensions.toMilliseconds
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

internal class WaitForElement(
    private val element: ElementReference,
    private val timeout: Duration,
    private val step: Duration
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return String.format(
            "Wait for element: %s, timeout = %s, step = %s",
            element.toReadableFormat(),
            timeout.toReadableFormat(),
            step.toReadableFormat()
        )
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        val startTime = System.currentTimeMillis()

        while ((System.currentTimeMillis() - startTime) <= timeout.toMilliseconds()) {
            val sleepResult = Sleep(step).execute(adbExecutor)
            if (sleepResult.isLeft()) {
                return sleepResult.toLeft()
            }

            val getUiTreeResult = GetUiTree().execute(adbExecutor)
            if (getUiTreeResult.isLeft()) {
                return getUiTreeResult.toLeft()
            }

            val rootNode = getUiTreeResult.unwrap()
            if (rootNode.hasElement(element)) {
                return Either.Right(Unit)
            }
        }

        return Either.Left(Exception("Failed to find element: $element"))
    }
}