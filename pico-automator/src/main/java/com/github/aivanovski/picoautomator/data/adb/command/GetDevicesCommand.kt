package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Device
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.util.StringUtils.NEW_LINE
import com.github.aivanovski.picoautomator.util.StringUtils.TAB

internal class GetDevicesCommand : AdbCommand<List<Device>> {

    override fun execute(environment: AdbEnvironment): Either<Exception, List<Device>> {
        val devicesResult = environment.run("devices")
        if (devicesResult.isLeft()) {
            return devicesResult.mapToLeft()
        }

        val devices = devicesResult.unwrap()
            .split(NEW_LINE)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filterNot { it.startsWith("List of devices") }
            .mapNotNull { parseDevice(it) }

        return Either.Right(devices)
    }

    private fun parseDevice(line: String): Device? {
        val values = line.split(TAB)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (values.size != 2) {
            return null
        }

        return Device(id = values.first())
    }
}