package com.github.aivanovski.picoautomator

import com.github.aivanovski.picoautomator.domain.entity.Flow

object PicoAutomatorDsl {

    fun newFlow(
        name: String,
        content: PicoAutomatorApi.() -> Unit
    ): Flow {
        return Flow(
            name = name,
            content = content
        )
    }
}