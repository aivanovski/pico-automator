package com.github.aivanovski.picoautomator.android.ui.domain.flow.steps

import com.github.aivanovski.picoautomator.android.ui.domain.flow.driver.AccessibilityDriverImpl
import com.github.aivanovski.picoautomator.android.ui.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.domain.entity.Either

class Launch(
    private val packageName: String
) : ExecutableFlowStep<Unit> {

    override fun describe(): String {
        return StringBuilder()
            .apply {
                append("Launch app: package name = $packageName")
            }
            .toString()
    }

    override fun execute(driver: Driver): Either<Exception, Unit> {
        return driver.launchApp(packageName)
    }
}