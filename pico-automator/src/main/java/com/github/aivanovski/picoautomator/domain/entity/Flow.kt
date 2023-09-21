package com.github.aivanovski.picoautomator.domain.entity

import com.github.aivanovski.picoautomator.PicoAutomatorApi

class Flow(
    val name: String,
    val content: PicoAutomatorApi.() -> Unit
)