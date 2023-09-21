package com.github.aivanovski.picoautomator.extensions

import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode
import java.util.LinkedList

internal fun UiTreeNode.matches(element: ElementReference): Boolean {
    return when (element) {
        is ElementReference.Id -> this.resourceId == "$packageName:id/${element.id}"
        is ElementReference.Text -> this.text == element.text
        is ElementReference.ContainsText -> {
            this.text != null && this.text.contains(element.text, ignoreCase = element.ignoreCase)
        }
    }
}

internal fun UiTreeNode.traverse(predicate: (UiTreeNode) -> Boolean): List<UiTreeNode> {
    val result = mutableListOf<UiTreeNode>()

    val nodes = LinkedList<UiTreeNode>()
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

fun UiTreeNode.findNode(predicate: (UiTreeNode) -> Boolean): UiTreeNode? {
    for (node in this.nodes) {
        val matched = node.traverse(predicate).firstOrNull()
        if (matched != null) {
            return matched
        }
    }
    return null
}

fun UiTreeNode.hasElement(element: ElementReference): Boolean {
    val matchedNodes = this.traverse { node -> node.matches(element) }
    return matchedNodes.isNotEmpty()
}