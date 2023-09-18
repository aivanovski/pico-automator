package com.github.aivanovski.picoautomator.extensions

inline fun <T> List<T>.indexOf(startIdx: Int, predicate: (T) -> Boolean): Int {
    for (i in startIdx until this.size) {
        if (predicate.invoke(this[i])) {
            return i
        }
    }
    return -1
}