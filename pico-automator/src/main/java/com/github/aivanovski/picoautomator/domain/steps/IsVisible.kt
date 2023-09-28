package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.extensions.toReadableFormat

internal class IsVisible(
    private val parentElement: ElementReference?,
    private val elements: List<ElementReference>
) : ExecutableFlowStep<Unit>, FlakyFlowStep {

    override fun describe(): String {
        return when {
            parentElement != null -> {
                String.format(
                    "Is visible: inside [%s] -> %s",
                    parentElement.toReadableFormat(),
                    elements.toReadableFormat()
                )
            }

            else -> "Is visible: ${elements.toReadableFormat()}"
        }
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        return AssertVisible(parentElement, elements).execute(adbExecutor)
    }
}