package com.github.aivanovski.picoautomator.android.ui.domain.flow.driver

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import com.github.aivanovski.picoautomator.android.ui.domain.entity.UiNode
import com.github.aivanovski.picoautomator.android.ui.extensions.convertToUiNode
import com.github.aivanovski.picoautomator.android.ui.extensions.dumpToString
import com.github.aivanovski.picoautomator.android.ui.extensions.findNode
import com.github.aivanovski.picoautomator.android.ui.extensions.formatShortDescription
import com.github.aivanovski.picoautomator.domain.entity.Either
import timber.log.Timber

class AccessibilityDriverImpl(
    private val context: Context,
    private val service: AccessibilityService
) : Driver {

    // private var convertedRootNode: UiTreeNode? = null
    // private var rootNode: AccessibilityNodeInfo? = null

    // fun onUiTreeUpdated(rootNode: AccessibilityNodeInfo) {
    //     // this.rootNode = rootNode
    //     // this.convertedRootNode = rootNode.convertToUiEntity().convertToUiNode()
    //     //
    //     // convertedRootNode?.printDump()
    // }

    override fun sendBroadcast(
        packageName: String,
        action: String,
        data: Map<String, String>
    ): Either<Exception, Unit> {
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

    override fun launchApp(packageName: String): Either<Exception, Unit> {
        Timber.d("launchApp: packageName=$packageName")

        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return Either.Left(Exception("Unable to find activity for package: $packageName"))

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)

        return Either.Right(Unit)
    }

    override fun getUiTree(): Either<Exception, UiNode<AccessibilityNodeInfo>> {
        val accessibilityNode = service.rootInActiveWindow

        val uiNode = accessibilityNode.convertToUiNode()

        Timber.d(uiNode.dumpToString())

        return Either.Right(uiNode)
    }

    override fun tapOn(uiNode: UiNode<AccessibilityNodeInfo>): Either<Exception, Unit> {
        val isPerformed = uiNode.source.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if (!isPerformed) {
            return Either.Left(Exception("Unable to perform action: ACTION_CLICK"))
        }

        return Either.Right(Unit)
    }

    // fun tapOn(element: ElementReference): Either<Exception, Unit> {
    //     val getUiTreeResult = getUiTree()
    //     if (getUiTreeResult.isLeft()) {
    //         return getUiTreeResult.mapToLeft()
    //     }
    //
    //     val uiNode = getUiTreeResult.unwrap()
    //
    //     val targetNode = uiNode.findNode { node -> node.matches(element) }
    //         ?: return Either.Left(Exception("Unable to find node: $element"))
    //
    //     Timber.d(
    //         "Matched node: node=%s",
    //         targetNode.formatShortDescription(),
    //     )
    //
    //     val isPerformed = targetNode.source.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    //     if (!isPerformed) {
    //         return Either.Left(Exception("Unable to perform action: click"))
    //     }
    //
    //     return Either.Right(Unit)
    // }

    override fun inputText(text: String): Either<Exception, Unit> {
        val getUiTreeResult = getUiTree()
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.mapToLeft()
        }

        val uiNode = getUiTreeResult.unwrap()

        val focusedNode = uiNode.findNode { node -> node.entity.isFocused == true }
            ?: return Either.Left(Exception("Unable to find focused node"))

        Timber.d(
            "Matched node: node=%s",
            focusedNode.formatShortDescription(),
        )

        val bundle = Bundle()
        bundle.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )

        // TODO: should be edit text
        val isPerformed = focusedNode.source.performAction(
            AccessibilityNodeInfo.ACTION_SET_TEXT,
            bundle
        )
        if (!isPerformed) {
            return Either.Left(Exception("Unable to perform action: ACTION_SET_TEXT"))
        }

        return Either.Right(Unit)
    }

    // fun tapOn(x: Int, y: Int): Either<Exception, Unit> {
    //     Timber.d("tap: x=$x, y=$y")
    //     val path = Path()
    //     path.moveTo(x.toFloat(), y.toFloat())
    //
    //     val gestureBuilder = Builder()
    //     gestureBuilder.addStroke(StrokeDescription(path, 0, 100))
    //
    //     val isDispatched = service.dispatchGesture(
    //         gestureBuilder.build(),
    //         object : GestureResultCallback() {
    //             override fun onCompleted(gestureDescription: GestureDescription) {
    //                 Timber.d("click  success " + LocalDateTime.now().toString())
    //                 // path.close()
    //             }
    //
    //             override fun onCancelled(gestureDescription: GestureDescription) {
    //                 Timber.d(" click  fail.")
    //                 // path.close()
    //             }
    //         },
    //         null
    //     )
    //
    //     if (!isDispatched) {
    //         return Either.Left(Exception("Unable to perform gesture: click x=$x, y=$y"))
    //     }
    //
    //     return Either.Right(Unit)
    // }
}
