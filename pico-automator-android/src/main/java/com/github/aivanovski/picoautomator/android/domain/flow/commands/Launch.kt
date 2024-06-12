package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException

class Launch(
    private val packageName: String
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "Launch app: package name = %s".format(packageName)
    }

    override suspend fun execute(driver: Driver): Either<FlowExecutionException, Unit> {
        return driver.launchApp(packageName)
    }
}