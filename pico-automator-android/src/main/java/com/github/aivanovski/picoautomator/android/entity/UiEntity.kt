package com.github.aivanovski.picoautomator.android.entity

import com.github.aivanovski.picoautomator.domain.entity.Bounds

data class UiEntity(
    val resourceId: String?,
    val packageName: String?,
    val className: String?,
    val bounds: Bounds?,
    val text: String?,
    val contentDescription: String?,
    val isEnabled: Boolean?,
    val isFocused: Boolean?,
    val isFocusable: Boolean?,
    val isClickable: Boolean?,
    val isLongClickable: Boolean?,
    val isCheckable: Boolean?,
    val isChecked: Boolean?
)