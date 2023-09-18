package com.github.aivanovski.picoautomator.domain.entity

data class UiNode(
    val resourceId: String?,
    val packageName: String?,
    val className: String?,
    val bounds: Bounds?,
    val text: String?,
    val nodes: MutableList<UiNode>
)