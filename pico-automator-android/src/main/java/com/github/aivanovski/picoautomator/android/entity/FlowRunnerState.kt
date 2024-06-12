package com.github.aivanovski.picoautomator.android.entity

enum class FlowRunnerState {
    STOPPED,
    IDLE,
    RUNNING,
    PENDING;

    companion object {
        fun fromName(name: String): FlowRunnerState? {
            return values().firstOrNull { state -> state.name == name }
        }
    }
}