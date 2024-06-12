package com.github.aivanovski.picoautomator.domain.newapi.entity

import kotlinx.serialization.Serializable

@Serializable
sealed interface KeyCode {

    @Serializable
    object Back : KeyCode

    @Serializable
    object Home : KeyCode
}