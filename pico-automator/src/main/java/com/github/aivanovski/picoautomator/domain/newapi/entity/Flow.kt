package com.github.aivanovski.picoautomator.domain.newapi.entity

data class Flow(
    val name: String,
    val steps: List<FlowStep>
)