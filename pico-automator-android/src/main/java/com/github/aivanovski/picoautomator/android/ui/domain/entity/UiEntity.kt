package com.github.aivanovski.picoautomator.android.ui.domain.entity

import com.github.aivanovski.picoautomator.domain.entity.Bounds

data class UiEntity(
    val resourceId: String?,
    val packageName: String?,
    val className: String?,
    val bounds: Bounds?,
    val text: String?,
    val contentDescription: String?,
    val isFocused: Boolean?,
)