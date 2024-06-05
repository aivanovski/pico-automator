package com.github.aivanovski.picoautomator.data.adb.entity

data class UiNodeEntity(
    var resourceId: String? = null,
    var packageName: String? = null,
    var className: String? = null,
    var bounds: String? = null,
    var text: String? = null,
    var contentDescription: String? = null,
    var isFocused: Boolean? = null,
    var nodes: MutableList<UiNodeEntity> = mutableListOf()
)