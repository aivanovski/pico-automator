package com.github.aivanovski.picoautomator.android.ui.extensions

import android.graphics.Rect
import com.github.aivanovski.picoautomator.domain.entity.Bounds

fun Bounds.toShortString(): String {
    return "Bounds[$left,$top:$right,$bottom]"
}

fun Rect.toBounds(): Bounds {
    return Bounds(
        left = left,
        right = right,
        top = top,
        bottom = bottom
    )
}