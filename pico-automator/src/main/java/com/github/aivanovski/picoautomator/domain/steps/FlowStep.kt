package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.domain.entity.Either

interface FlowStep {
    fun describe(): String
    fun execute(adbExecutor: AdbExecutor): Either<Exception, Unit>
}