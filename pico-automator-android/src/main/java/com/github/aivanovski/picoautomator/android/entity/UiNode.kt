package com.github.aivanovski.picoautomator.android.entity

data class UiNode<T>(
    val source: T,
    val entity: UiEntity,
    // TODO: replace with regular List
    val nodes: MutableList<UiNode<T>>
)