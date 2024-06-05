package com.github.aivanovski.picoautomator.android.ui.domain.flow.driver

import android.view.accessibility.AccessibilityNodeInfo
import com.github.aivanovski.picoautomator.android.ui.domain.entity.UiNode
import com.github.aivanovski.picoautomator.domain.entity.Either

interface Driver {
    fun sendBroadcast(
        packageName: String,
        action: String,
        data: Map<String, String>
    ): Either<Exception, Unit>

    fun launchApp(packageName: String): Either<Exception, Unit>
    fun getUiTree(): Either<Exception, UiNode<AccessibilityNodeInfo>>
    fun tapOn(uiNode: UiNode<AccessibilityNodeInfo>): Either<Exception, Unit>
    fun inputText(text: String): Either<Exception, Unit>
}