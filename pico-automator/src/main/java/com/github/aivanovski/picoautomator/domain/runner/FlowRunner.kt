package com.github.aivanovski.picoautomator.domain.runner

import com.github.aivanovski.picoautomator.domain.steps.FlakyFlowStep
import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.GetDevicesCommand
import com.github.aivanovski.picoautomator.data.process.ProcessExecutor
import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.steps.FlowStep

class FlowRunner(
    private val maxFlakyStepRepeatCount: Int = MAX_REPEAT_COUNT,
    private val callbacks: FlowRunnerCallbacks? = null
) {

    private val processExecutor = ProcessExecutor()
    private val adbExecutor = AdbExecutor(
        AdbEnvironment(
            processExecutor = processExecutor,
            device = null
        )
    )

    fun run(flow: Flow) {
        val selectDeviceResult = selectDevice()
        if (selectDeviceResult.isLeft()) {
            callbacks?.onFlowFinished(flow, selectDeviceResult.mapToLeft())
            return
        }

        val device = selectDeviceResult.unwrap()
        val adbDeviceExecutor = adbExecutor.cloneWithEnvironment(
            AdbEnvironment(
                processExecutor = processExecutor,
                device = device
            )
        )
        callbacks?.onDeviceSelected(device)

        val result = runInternal(
            adbExecutor = adbDeviceExecutor,
            flow = flow,
            isPredecessor = false
        )

        callbacks?.onFlowFinished(flow, result)
    }

    private fun runInternal(
        adbExecutor: AdbExecutor,
        flow: Flow,
        isPredecessor: Boolean
    ): Either<Exception, Unit> {
        if (isPredecessor) {
            callbacks?.onFlowStarted(flow, isPredecessor)
        }

        if (flow.predecessors.isNotEmpty()) {
            for (predecessor in flow.predecessors) {
                val result = runInternal(
                    adbExecutor = adbExecutor,
                    flow = predecessor,
                    isPredecessor = true
                )
                if (result.isLeft()) {
                    return result
                }
            }
        }

        if (!isPredecessor) {
            callbacks?.onFlowStarted(flow, isPredecessor)
        }

        var stepIndex = 0
        var repeatCount = 0

        while (stepIndex < flow.steps.size) {
            val step = flow.steps[stepIndex]

            callbacks?.onStepStarted(flow, step, repeatCount)

            val result = step.execute(adbExecutor)

            callbacks?.onStepFinished(flow, step, result)

            when {
                shouldRepeatStep(step, result, repeatCount) -> {
                    repeatCount++
                    continue
                }

                isFailed(result) -> {
                    return result
                }

                else -> {
                    stepIndex++
                    repeatCount = 0
                }
            }
        }

        return Either.Right(Unit)
    }

    private fun selectDevice(): Either<Exception, Device> {
        val getDevicesResult = adbExecutor.execute(GetDevicesCommand())
        if (getDevicesResult.isLeft()) {
            return getDevicesResult.mapToLeft()
        }

        val devices = getDevicesResult.unwrap()
        if (devices.isEmpty()) {
            return Either.Left(Exception("No connected devices found"))
        }

        return Either.Right(devices.first())
    }

    private fun shouldRepeatStep(
        step: FlowStep,
        result: Either<Exception, Unit>,
        repeatCount: Int
    ): Boolean {
        return result.isLeft() &&
            step is FlakyFlowStep &&
            repeatCount < maxFlakyStepRepeatCount
    }

    private fun isFailed(result: Either<Exception, Unit>): Boolean {
        return result.isLeft()
    }

    companion object {
        private const val MAX_REPEAT_COUNT = 3
    }
}