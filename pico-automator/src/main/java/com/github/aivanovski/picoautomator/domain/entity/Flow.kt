package com.github.aivanovski.picoautomator.domain.entity

import com.github.aivanovski.picoautomator.domain.steps.FlowStep

data class Flow(
    val name: String,
    val packageName: String,
    val predecessors: List<Flow>,
    val steps: List<FlowStep>
)