package com.github.aivanovski.picoautomator.data.adb

import com.github.aivanovski.picoautomator.data.process.ProcessExecutor
import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import org.buildobjects.process.TimeoutException

internal class AdbEnvironment(
    private val processExecutor: ProcessExecutor,
    val device: Device?
) {

    fun run(
        adbCommand: String
    ): Either<Exception, String> {
        val command = if (device != null) {
            "adb -s ${device.id} $adbCommand"
        } else {
            "adb $adbCommand"
        }

        var result: Either<Exception, String> = processExecutor.run(command)
        var exception = result.unwrapErrorOrNull()
        var attemptIdx = 1

        while (exception?.shouldRetry() == true && attemptIdx < MAX_TIMEOUT_RETRY_COUNT) {
            result = processExecutor.run(command)
            exception = result.unwrapErrorOrNull()

            attemptIdx++
        }

        return result
    }

    private fun Exception.shouldRetry(): Boolean =
        this is TimeoutException

    companion object {
        private const val MAX_TIMEOUT_RETRY_COUNT = 5
    }
}