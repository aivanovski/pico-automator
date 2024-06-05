package com.github.aivanovski.picoautomator.android.ui.domain.flow.steps

import com.github.aivanovski.picoautomator.android.ui.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.domain.entity.Either
import java.lang.StringBuilder

class Broadcast(
    private val packageName: String,
    private val action: String,
    private val data: Map<String, String>
) : ExecutableFlowStep<Unit> {

    override fun describe(): String {
        return StringBuilder()
            .apply {
                append("Broadcast: $packageName/$action")
                if (data.isNotEmpty()) {
                    append(" [")
                    for ((key, value) in data.entries) {
                        if (endsWith("[")) {
                            append(", ")
                        }
                        append("$key=$value")
                    }

                    append("]")
                }
            }
            .toString()
    }

    override fun execute(driver: Driver): Either<Exception, Unit> {
        return driver.sendBroadcast(
            packageName = packageName,
            action = action,
            data = data
        )
    }
}