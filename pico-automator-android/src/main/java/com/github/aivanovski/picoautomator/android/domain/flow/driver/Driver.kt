package com.github.aivanovski.picoautomator.android.domain.flow.driver

import android.view.accessibility.AccessibilityNodeInfo
import com.github.aivanovski.picoautomator.android.entity.UiNode
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.KeyCode
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.DriverException

interface Driver {
    fun sendBroadcast(
        packageName: String,
        action: String,
        data: Map<String, String>
    ): Either<DriverException, Unit>

    fun launchApp(packageName: String): Either<DriverException, Unit>
    fun getUiTree(): Either<DriverException, UiNode<AccessibilityNodeInfo>>
    fun tapOn(uiNode: UiNode<AccessibilityNodeInfo>): Either<DriverException, Unit>
    fun longTapOn(uiNode: UiNode<AccessibilityNodeInfo>): Either<DriverException, Unit>
    fun inputText(text: String, uiNode: UiNode<AccessibilityNodeInfo>): Either<DriverException, Unit>
    fun pressKey(key: KeyCode): Either<DriverException, Unit>
}