package com.github.aivanovski.picoautomator.domain.runner

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.GetDevicesCommand
import com.github.aivanovski.picoautomator.data.process.ProcessExecutor
import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.steps.StepCommand

class FlowRunner(
    private val maxFlakyStepRepeatCount: Int = MAX_REPEAT_COUNT
) {

    private var device: Device? = null
    private val listeners = mutableListOf<FlowLifecycleListener>()
    private val processExecutor = ProcessExecutor()
    private val adbExecutor = AdbExecutor(
        AdbEnvironment(
            processExecutor = processExecutor,
            device = null
        )
    )

    fun run(
        flow: Flow,
        isUsePreviouslySelectedDevice: Boolean = true
    ) {
        val lastDevice = device

        val device = if (lastDevice == null || !isUsePreviouslySelectedDevice) {
            val selectDeviceResult = selectDevice()
            if (selectDeviceResult.isLeft()) {
                listeners.forEach { it.onFlowFinished(flow, selectDeviceResult.toLeft()) }
                return
            }

            val device = selectDeviceResult.unwrap()
            listeners.forEach { it.onDeviceSelected(device) }

            device
        } else {
            lastDevice
        }

        this.device = device

        val adbDeviceExecutor = adbExecutor.cloneWithEnvironment(
            AdbEnvironment(
                processExecutor = processExecutor,
                device = device
            )
        )

        ApiImpl(
            flow = flow,
            adbExecutor = adbDeviceExecutor,
            processExecutor = processExecutor,
            maxFlakyStepRepeatCount = maxFlakyStepRepeatCount,
            lifecycleListener = createFlowLifecycleListener()
        ).run()
    }

    fun addLifecycleListener(listener: FlowLifecycleListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeLifecycleListener(listener: FlowLifecycleListener) {
        listeners.remove(listener)
    }

    fun removeAllListeners() {
        listeners.clear()
    }

    private fun selectDevice(): Either<Exception, Device> {
        val getDevicesResult = adbExecutor.execute(GetDevicesCommand())
        if (getDevicesResult.isLeft()) {
            return getDevicesResult.toLeft()
        }

        val devices = getDevicesResult.unwrap()
        if (devices.isEmpty()) {
            return Either.Left(Exception("No connected devices found"))
        }

        return Either.Right(devices.first())
    }

    private fun createFlowLifecycleListener(): FlowLifecycleListener {
        return object : FlowLifecycleListener {
            override fun onDeviceSelected(device: Device) {
                listeners.forEach { it.onDeviceSelected(device) }
            }

            override fun onFlowStarted(flow: Flow, isPredecessor: Boolean) {
                listeners.forEach { it.onFlowStarted(flow, isPredecessor) }
            }

            override fun onFlowFinished(flow: Flow, result: Either<Exception, Any>) {
                listeners.forEach { it.onFlowFinished(flow, result) }
            }

            override fun onStepStarted(
                flow: Flow,
                step: StepCommand,
                stepIndex: Int,
                repeatCount: Int
            ) {
                listeners.forEach { it.onStepStarted(flow, step, stepIndex, repeatCount) }
            }

            override fun onStepFinished(
                flow: Flow,
                step: StepCommand,
                stepIndex: Int,
                result: Either<Exception, Any>
            ) {
                listeners.forEach { it.onStepFinished(flow, step, stepIndex, result) }
            }
        }
    }

    companion object {
        private const val MAX_REPEAT_COUNT = 3

        @JvmStatic
        var defaultRunner = FlowRunner(maxFlakyStepRepeatCount = MAX_REPEAT_COUNT)
    }
}