package com.github.aivanovski.picoautomator.data.adb

import com.github.aivanovski.picoautomator.data.process.ProcessExecutor
import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either

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

        return processExecutor.run(command)
    }
}