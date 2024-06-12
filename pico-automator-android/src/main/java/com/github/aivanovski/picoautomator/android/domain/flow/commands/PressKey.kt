package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.KeyCode
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException

class PressKey(
    private val key: KeyCode
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        val name = when (key) {
            KeyCode.Back -> "Back"
            KeyCode.Home -> "Home"
        }

        return "Press key: %s".format(name)
    }

    override suspend fun execute(driver: Driver): Either<FlowExecutionException, Unit> {
        return driver.pressKey(key)
    }
}