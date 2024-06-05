package com.github.aivanovski.picoautomator.domain.entity

data class Bounds(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    fun centerX(): Int = (left + right) / 2
    fun centerY(): Int = (top + bottom) / 2
}