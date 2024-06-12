package com.github.aivanovski.picoautomator.domain.entity

import com.github.aivanovski.picoautomator.PicoAutomatorApi

// TODO: rename
class Flow(
    val name: String,
    val content: PicoAutomatorApi.() -> Unit
)