package com.github.aivanovski.picoautomator.android.ui.extensions

fun Int.getFlags(possibleFlags: Map<Int, String>): String {
    if (this == 0) return "No flags"

    val result = mutableListOf<String>()

    for ((flag, flagName) in possibleFlags.entries) {
        if ((this and flag) != 0) {
            result.add(flagName)
        }
    }

    return result.joinToString(separator = " | ")
}