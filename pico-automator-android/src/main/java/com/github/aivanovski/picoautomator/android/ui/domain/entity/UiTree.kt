package com.github.aivanovski.picoautomator.android.ui.domain.entity

import android.view.accessibility.AccessibilityNodeInfo
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode

data class UiTree(
    val accessibilityRootNode: AccessibilityNodeInfo,
    val uiTreeRootNode: UiTreeNode
)