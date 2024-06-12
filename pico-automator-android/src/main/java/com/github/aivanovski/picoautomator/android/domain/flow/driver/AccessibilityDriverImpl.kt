package com.github.aivanovski.picoautomator.android.domain.flow.driver

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import com.github.aivanovski.picoautomator.android.entity.UiNode
import com.github.aivanovski.picoautomator.android.extensions.convertToUiNode
import com.github.aivanovski.picoautomator.android.extensions.dumpToString
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.KeyCode
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.DriverException
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FailedToGetUiNodesException
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FailedToPerformActionException
import timber.log.Timber

class AccessibilityDriverImpl(
    private val context: Context,
    private val service: AccessibilityService
) : Driver {

    override fun sendBroadcast(
        packageName: String,
        action: String,
        data: Map<String, String>
    ): Either<DriverException, Unit> {
        val intent = Intent()
            .apply {
                for ((key, value) in data.entries) {
                    putExtra(key, value)
                }

                setComponent(
                    ComponentName(
                        packageName,
                        action
                    )
                )
            }

        context.sendBroadcast(intent)

        return Either.Right(Unit)
    }

    override fun launchApp(packageName: String): Either<DriverException, Unit> {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return Either.Left(
                DriverException("Unable to find activity for package: $packageName")
            )

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)

        return Either.Right(Unit)
    }

    override fun getUiTree(): Either<DriverException, UiNode<AccessibilityNodeInfo>> {
        val accessibilityNode = service.rootInActiveWindow
            ?: return Either.Left(FailedToGetUiNodesException())

        val uiNode = accessibilityNode.convertToUiNode()

        Timber.d(uiNode.dumpToString())

        return Either.Right(uiNode)
    }

    override fun tapOn(uiNode: UiNode<AccessibilityNodeInfo>): Either<DriverException, Unit> {
        val isPerformed = uiNode.source.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if (!isPerformed) {
            return Either.Left(FailedToPerformActionException("ACTION_CLICK"))
        }

        return Either.Right(Unit)
    }

    override fun longTapOn(uiNode: UiNode<AccessibilityNodeInfo>): Either<DriverException, Unit> {
        val isPerformed = uiNode.source.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
        if (!isPerformed) {
            return Either.Left(FailedToPerformActionException("ACTION_LONG_CLICK"))
        }

        return Either.Right(Unit)
    }

    override fun inputText(
        text: String,
        uiNode: UiNode<AccessibilityNodeInfo>
    ): Either<DriverException, Unit> {
        val bundle = Bundle()
        bundle.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )

        // TODO: add check that node is editable
        val isPerformed = uiNode.source.performAction(
            AccessibilityNodeInfo.ACTION_SET_TEXT,
            bundle
        )
        if (!isPerformed) {
            return Either.Left(FailedToPerformActionException("ACTION_SET_TEXT"))
        }

        return Either.Right(Unit)
    }

    override fun pressKey(key: KeyCode): Either<DriverException, Unit> {
        val action = KEY_ACTIONS[key]
            ?: return Either.Left(DriverException("Unable to determine action for key: $key"))

        val isPerformed = service.performGlobalAction(action.id)
        if (!isPerformed) {
            return Either.Left(FailedToPerformActionException(action.name))
        }

        return Either.Right(Unit)
    }

    private data class AccessibilityAction(
        val name: String,
        val id: Int
    )

    companion object {
        private val KEY_ACTIONS = mapOf(
            KeyCode.Back to AccessibilityAction(
                name = "GLOBAL_ACTION_BACK",
                id = AccessibilityService.GLOBAL_ACTION_BACK
            ),

            KeyCode.Home to AccessibilityAction(
                name = "GLOBAL_ACTION_HOME",
                id = AccessibilityService.GLOBAL_ACTION_HOME
            )
        )
    }
}
