package com.github.aivanovski.picoautomator.android.ui.domain.flow

import com.github.aivanovski.picoautomator.android.ui.UiAccessibilityService
import com.github.aivanovski.picoautomator.android.ui.data.Settings
import com.github.aivanovski.picoautomator.android.ui.data.api.entity.FlowStep
import com.github.aivanovski.picoautomator.android.ui.data.entity.StepAction
import com.github.aivanovski.picoautomator.android.ui.domain.FlowInteractor
import com.github.aivanovski.picoautomator.android.ui.domain.flow.driver.AccessibilityDriverImpl
import com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.Assert
import com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.Broadcast
import com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.ExecutableFlowStep
import com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.InputText
import com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.Launch
import com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.Tap
import com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.assertion.NotVisibleAssertion
import com.github.aivanovski.picoautomator.android.ui.domain.flow.steps.assertion.VisibleAssertion
import com.github.aivanovski.picoautomator.domain.entity.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

class FlowRunner(
    private val settings: Settings,
    private val interactor: FlowInteractor,
    private val driver: AccessibilityDriverImpl
) {

    private val state = AtomicInteger(STATE_IDLE)
    private val stepIndex = AtomicInteger(0)
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main)

    fun isRunning(): Boolean = state.get() == STATE_RUNNING

    fun isFailed(): Boolean = state.get() == STATE_FAILED

    fun reset() {
        state.set(STATE_IDLE)
        stepIndex.set(0)
    }

    fun start() {
        state.set(STATE_RUNNING)
        startNext(isRetry = false)
    }

    fun stop() {
        state.set(STATE_IDLE)
        job.cancel()
    }

    private fun startNext(isRetry: Boolean) {
        scope.launch {
            delay(1000L)

            if (!isRunning()) {
                Timber.d("Cancelled")
                return@launch
            }

            val getCurrentStep = interactor.getCurrentStepInfo()
            if (getCurrentStep.isLeft()) {
                settings.testRunnerState = UiAccessibilityService.STATE_IDLE
                return@launch
            }

            val entry = getCurrentStep.unwrap()
            val step = createStep(entry?.command!!)
            val index = stepIndex.get()

            val stepTitle = if (isRetry) "Retry" else "Step"
            Timber.d("$stepTitle ${index + 1}: ${step.describe()}")
            val result = step.execute(driver)
            Timber.d("$stepTitle ${index + 1}: ${result.shortResultText()}")

            val getNextActionResult = interactor.onStepFinished(entry, result)
            if (getNextActionResult.isLeft()) {
                Timber.e("Error has been occurred during step ${step.describe()}: $result")
                settings.testRunnerState = UiAccessibilityService.STATE_IDLE
                settings.currentStepUid = null
                state.set(STATE_FAILED)
                return@launch
            }

            if (result.isRight()) {
                stepIndex.incrementAndGet()
            }

            val nextAction = getNextActionResult.unwrap()
            Timber.d("nextAction=$nextAction")
            when (nextAction) {
                StepAction.NEXT -> {
                    startNext(isRetry = false)
                }

                StepAction.COMPLETE -> {
                    Timber.d("Flow finished successfully!")
                    settings.testRunnerState = UiAccessibilityService.STATE_IDLE
                    settings.currentStepUid = null
                    state.set(STATE_IDLE)
                }

                StepAction.RETRY -> {
                    startNext(isRetry = true)
                }

                StepAction.STOP -> {
                    settings.testRunnerState = UiAccessibilityService.STATE_IDLE
                    settings.currentStepUid = null
                    state.set(STATE_FAILED)
                }
            }
        }
    }

    private fun Either<*, *>.shortResultText(): String {
        return if (isLeft()) {
            "FAILED"
        } else {
            "SUCCESS"
        }
    }

    private fun createStep(step: FlowStep): ExecutableFlowStep<*> {
        return when (step) {
            is FlowStep.SendBroadcast -> {
                Broadcast(
                    packageName = step.packageName,
                    action = step.action,
                    data = step.data
                )
            }

            is FlowStep.Launch -> {
                Launch(
                    packageName = step.packageName,
                )
            }

            is FlowStep.AssertVisible -> {
                Assert(
                    parentElement = null,
                    elements = step.elements,
                    assertion = VisibleAssertion()
                )
            }

            is FlowStep.AssertNotVisible -> {
                Assert(
                    parentElement = null,
                    elements = step.elements,
                    assertion = NotVisibleAssertion()
                )
            }

            is FlowStep.TapOn -> {
                Tap(element = step.element)
            }

            is FlowStep.InputText -> {
                InputText(
                    text = step.text,
                    element = step.element
                )
            }
        }
    }

    companion object {
        private const val STATE_IDLE = 1
        private const val STATE_RUNNING = 2
        private const val STATE_FAILED = 3
    }
}