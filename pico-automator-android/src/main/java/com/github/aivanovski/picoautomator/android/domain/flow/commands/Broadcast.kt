package com.github.aivanovski.picoautomator.android.domain.flow.commands

import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FlowExecutionException
import java.lang.StringBuilder

class Broadcast(
    private val packageName: String,
    private val action: String,
    private val data: Map<String, String>
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return StringBuilder()
            .apply {
                append("Broadcast: $packageName/$action")
                if (data.isNotEmpty()) {
                    append(" [")

                    for ((key, value) in data.entries) {
                        if (!endsWith("[")) {
                            append(", ")
                        }
                        append("$key=$value")
                    }

                    append("]")
                }
            }
            .toString()
    }

    override suspend fun execute(driver: Driver): Either<FlowExecutionException, Unit> {
        return driver.sendBroadcast(
            packageName = packageName,
            action = action,
            data = data
        )
    }
}