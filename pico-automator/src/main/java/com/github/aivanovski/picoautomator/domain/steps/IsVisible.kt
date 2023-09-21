package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference

internal class IsVisible(
    private val parentElement: ElementReference?,
    private val elements: List<ElementReference>
) : ExecutableFlowStep<Unit>, FlakyFlowStep {

    override fun describe(): String {
        return when {
            parentElement != null -> {
                "Is visible: inside [$parentElement] -> $elements"
            }

            else -> "Is visible: $elements"
        }
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit> {
        return AssertVisible(parentElement, elements).execute(adbExecutor)
    }
}