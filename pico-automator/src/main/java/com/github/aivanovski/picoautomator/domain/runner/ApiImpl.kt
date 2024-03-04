package com.github.aivanovski.picoautomator.domain.runner

import com.github.aivanovski.picoautomator.PicoAutomatorApi
import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.process.ProcessExecutor
import com.github.aivanovski.picoautomator.domain.entity.Duration
import com.github.aivanovski.picoautomator.domain.entity.Duration.Companion.millis
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode
import com.github.aivanovski.picoautomator.domain.entity.exception.CompleteExecutionException
import com.github.aivanovski.picoautomator.domain.entity.exception.ExecutionException
import com.github.aivanovski.picoautomator.domain.entity.exception.FailedStepException
import com.github.aivanovski.picoautomator.domain.entity.exception.StopExecutionException
import com.github.aivanovski.picoautomator.domain.steps.Assert
import com.github.aivanovski.picoautomator.domain.steps.ExecutableFlowStep
import com.github.aivanovski.picoautomator.domain.steps.FlakyFlowStep
import com.github.aivanovski.picoautomator.domain.steps.GetUiTree
import com.github.aivanovski.picoautomator.domain.steps.InputText
import com.github.aivanovski.picoautomator.domain.steps.IsVisible
import com.github.aivanovski.picoautomator.domain.steps.Launch
import com.github.aivanovski.picoautomator.domain.steps.LongTap
import com.github.aivanovski.picoautomator.domain.steps.PressBack
import com.github.aivanovski.picoautomator.domain.steps.PressKey
import com.github.aivanovski.picoautomator.domain.steps.Shell
import com.github.aivanovski.picoautomator.domain.steps.Sleep
import com.github.aivanovski.picoautomator.domain.steps.Tap
import com.github.aivanovski.picoautomator.domain.steps.WaitForElement
import com.github.aivanovski.picoautomator.domain.steps.assertions.NotVisibleAssertion
import com.github.aivanovski.picoautomator.domain.steps.assertions.VisibleAssertion

internal class ApiImpl(
    private val flow: Flow,
    private val adbExecutor: AdbExecutor,
    private val processExecutor: ProcessExecutor,
    private val maxFlakyStepRepeatCount: Int,
    private val lifecycleListener: FlowLifecycleListener?
) : PicoAutomatorApi {

    private val steps = mutableListOf<Pair<ExecutableFlowStep<Any>, List<Either<Exception, Any>>>>()
    private var stepIndex = 0

    fun run() {
        lifecycleListener?.onFlowStarted(flow, isPredecessor = false)

        try {
            flow.content.invoke(this)
            val result = steps.last().second.last()
            lifecycleListener?.onFlowFinished(flow, result)
        } catch (exception: StopExecutionException) {
            lifecycleListener?.onFlowFinished(flow, Either.Left(exception))
        } catch (exception: CompleteExecutionException) {
            lifecycleListener?.onFlowFinished(flow, Either.Right(exception.completionMessage))
        } catch (exception: FailedStepException) {
            lifecycleListener?.onFlowFinished(flow, exception.result)
        } catch (exception: ExecutionException) {
            exception.printStackTrace()
            lifecycleListener?.onFlowFinished(flow, Either.Left(exception))
        }
    }

    override fun launch(packageName: String, isClearState: Boolean): Either<Exception, Unit> {
        return runStep(Launch(packageName, isClearState = isClearState))
    }

    override fun assertVisible(elements: List<ElementReference>): Either<Exception, Unit> {
        return runStep(
            Assert(
                parentElement = null,
                elements = elements,
                assertion = VisibleAssertion()
            )
        )
    }

    override fun assertVisible(element: ElementReference): Either<Exception, Unit> {
        return assertVisible(listOf(element))
    }

    override fun assertNotVisible(elements: List<ElementReference>): Either<Exception, Unit> {
        return runStep(
            Assert(
                parentElement = null,
                elements = elements,
                assertion = NotVisibleAssertion()
            )
        )
    }

    override fun assertNotVisible(element: ElementReference): Either<Exception, Unit> {
        return assertNotVisible(listOf(element))
    }

    override fun tapOn(element: ElementReference): Either<Exception, Unit> {
        return runStep(
            Tap(element = element)
        )
    }

    override fun longTapOn(element: ElementReference): Either<Exception, Unit> {
        return runStep(
            LongTap(element = element)
        )
    }

    override fun inputText(text: String): Either<Exception, Unit> {
        return runStep(
            InputText(
                text = text,
                element = null
            )
        )
    }

    override fun inputText(element: ElementReference, text: String): Either<Exception, Unit> {
        return runStep(
            InputText(
                text = text,
                element = element
            )
        )
    }

    override fun pressBack(): Either<Exception, Unit> {
        return runStep(PressBack())
    }

    override fun pressKey(keyCode: String): Either<Exception, Unit> {
        return runStep(PressKey(keyCode))
    }

    override fun isVisible(element: ElementReference): Boolean {
        return runStep(
            IsVisible(
                parentElement = null,
                elements = listOf(element)
            )
        ).unwrap()
    }

    override fun getUiTree(): UiTreeNode {
        return runStep(GetUiTree()).unwrap()
    }

    override fun waitUntil(
        element: ElementReference,
        timeout: Duration
    ): Either<Exception, Unit> {
        return waitUntil(element, timeout, step = millis(1000))
    }

    override fun waitUntil(
        element: ElementReference,
        timeout: Duration,
        step: Duration
    ): Either<Exception, Unit> {
        return runStep(
            WaitForElement(
                element = element,
                timeout = timeout,
                step = step
            )
        )
    }

    override fun sleep(duration: Duration): Either<Exception, Unit> {
        return runStep(Sleep(duration))
    }

    override fun fail(message: String) {
        throw StopExecutionException(message)
    }

    override fun complete(message: String) {
        throw CompleteExecutionException(message)
    }

    override fun shell(command: String) {
        runStep(Shell(processExecutor, command))
    }

    private fun <T : Any> runStep(
        step: ExecutableFlowStep<T>
    ): Either<Exception, T> {
        val results = mutableListOf<Either<Exception, T>>()

        var repeatCount = 0

        do {
            lifecycleListener?.onStepStarted(flow, step, stepIndex, repeatCount)

            val result = step.execute(adbExecutor)
            results.add(result)

            lifecycleListener?.onStepFinished(flow, step, stepIndex, result)

            val shouldRetry = when {
                shouldRepeatStep(step, result, repeatCount) -> {
                    repeatCount++
                    true
                }

                result.isLeft() -> {
                    false
                }

                else -> false
            }
        } while (shouldRetry)

        steps.add(step to results)
        stepIndex++

        val lastResult = results.last()
        if (lastResult.isLeft()) {
            throw FailedStepException(step, lastResult)
        }

        return lastResult
    }

    private fun shouldRepeatStep(
        step: ExecutableFlowStep<Any>,
        result: Either<Exception, Any>,
        repeatCount: Int
    ): Boolean {
        return result.isLeft() &&
            step is FlakyFlowStep &&
            repeatCount < maxFlakyStepRepeatCount - 1
    }
}