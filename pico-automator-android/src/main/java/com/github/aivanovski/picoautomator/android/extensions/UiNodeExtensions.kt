package com.github.aivanovski.picoautomator.android.extensions

import com.github.aivanovski.picoautomator.android.entity.UiNode
import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import java.lang.StringBuilder
import java.util.LinkedList

private const val DUMP_SEPARATOR = ", "
private const val DUMP_SEPARATOR_INDICATOR = "["

fun UiNode<*>.matches(selector: UiElementSelector): Boolean {
    return entity.matches(selector)
}

fun <T> UiNode<T>.traverseAndCollect(
    predicate: (UiNode<T>) -> Boolean
): List<UiNode<T>> {
    val result = mutableListOf<UiNode<T>>()

    val nodes = LinkedList<UiNode<T>>()
    nodes.addAll(this.nodes)

    while (nodes.isNotEmpty()) {
        repeat(nodes.size) {
            val node = nodes.removeFirst()

            if (predicate(node)) {
                result.add(node)
            }

            nodes.addAll(node.nodes)
        }
    }

    return result
}

fun <T> UiNode<T>.findNode(
    predicate: (UiNode<T>) -> Boolean
): UiNode<T>? {
    return traverseAndCollect(predicate).firstOrNull()
}

fun <T> UiNode<T>.getNodeParents(
    target: UiNode<T>
): List<UiNode<T>> {
    val result = mutableListOf<UiNode<T>>()

    this.traverseParents(target, result)

    return result.reversed()
}

private fun <T> UiNode<T>.traverseParents(
    target: UiNode<T>,
    result: MutableList<UiNode<T>>
): Boolean {
    if (this == target) {
        return true
    }

    for (childNode in nodes) {
        if (childNode.traverseParents(target, result)) {
            result.add(this)
            return true
        }
    }

    return false
}

fun UiNode<*>.hasElement(element: UiElementSelector): Boolean {
    val matchedNodes = this.traverseAndCollect { node -> node.matches(element) }
    return matchedNodes.isNotEmpty()
}

fun <T> UiNode<T>.dumpToString(): String {
    val lines = mutableListOf<String>()

    visitWithDepth { node, depth ->
        val indent = "  ".repeat(depth)
        lines.add("$indent${node.formatShortDescription()}")
    }

    return lines.joinToString(separator = "\n")
}

fun <T> UiNode<T>.visitWithDepth(
    visitor: (node: UiNode<T>, depth: Int) -> Unit
) {
    val nodes = LinkedList<Pair<Int, UiNode<T>>>()
    nodes.add(0 to this)

    while (nodes.isNotEmpty()) {
        val (depth, node) = nodes.pop()

        visitor.invoke(node, depth)

        for (childNode in node.nodes.reversed()) {
            nodes.push((depth + 1) to childNode)
        }
    }
}

fun UiNode<*>.formatShortDescription(): String {
    val node = this

    return StringBuilder()
        .apply {
            val className = node.entity.className

            val id = node.entity.resourceId
            val text = node.entity.text
            val cd = node.entity.contentDescription
            val bounds = node.entity.bounds?.toShortString()
            val isFocused = node.entity.isFocused
            val isClickable = node.entity.isClickable
            val childCount = node.nodes.size

            if (className != null) {
                val lastDot = className.lastIndexOf(".")
                if (lastDot != -1) {
                    append(className.substring(lastDot + 1))
                } else {
                    append(className)
                }
            }

            append("[")

            if (childCount > 0) {
                append("children=$childCount")
            }

            if (id != null) {
                appendWithSeparator("id=$id", DUMP_SEPARATOR, DUMP_SEPARATOR_INDICATOR)
            }

            if (text != null) {
                appendWithSeparator("text=$text", DUMP_SEPARATOR, DUMP_SEPARATOR_INDICATOR)
            }

            if (cd != null) {
                appendWithSeparator("contDesc=$cd", DUMP_SEPARATOR, DUMP_SEPARATOR_INDICATOR)
            }

            if (bounds != null) {
                appendWithSeparator("bounds=$bounds", DUMP_SEPARATOR, DUMP_SEPARATOR_INDICATOR)
            }

            if (isFocused == true) {
                appendWithSeparator("FOCUSED", DUMP_SEPARATOR, DUMP_SEPARATOR_INDICATOR)
            }

            if (isClickable == true) {
                appendWithSeparator("CLICKABLE", DUMP_SEPARATOR, DUMP_SEPARATOR_INDICATOR)
            }

            append("]")
        }
        .toString()
}

private fun StringBuilder.appendWithSeparator(
    value: String,
    separator: String,
    separatorIndicator: String
) {
    if (!this.endsWith(separatorIndicator)) {
        append(separator)
    }
    append(value)
}