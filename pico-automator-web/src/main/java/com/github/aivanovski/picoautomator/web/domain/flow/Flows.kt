package com.github.aivanovski.picoautomator.web.domain.flow

import com.github.aivanovski.picoautomator.webapi.FlowsItemDto
import com.github.aivanovski.picoautomator.webapi.UiElement
import javax.swing.text.FlowView.FlowStrategy

object Flows {


    private const val PROJECT_NAME = "keepassvault"
    private const val PROJECT_ID = "0xkeepassvault"
    private const val TEST_DATA_RECEIVER_ACITON =
        "com.ivanovsky.passnotes.domain.test.TestDataBroadcastReceiver"

    // private fun createUnlockFlow(): FlowsItemDto {
    //     val steps = FlowDsl.steps(
    //         packageName = "com.ivanovsky.passnotes",
    //         uid = "$PROJECT_ID/login"
    //     ) {
    //         sendBroadcast(
    //             action = TEST_DATA_RECEIVER_ACITON,
    //             data = mapOf("isResetAppData" to "true")
    //         )
    //         sendBroadcast(
    //             action = TEST_DATA_RECEIVER_ACITON,
    //             data = mapOf("fakeFileName" to "passwords.kdbx")
    //         )
    //         launch()
    //         assertVisible(UiElement.Text("passwords.kdbx"))
    //         tapOn(UiElement.Text("Password"))
    //         inputText("abc123")
    //         tapOn(UiElement.ContentDescription("unlockButton"))
    //         assertVisible(UiElement.Text("DISABLE"))
    //         tapOn(UiElement.Text("DISABLE"))
    //         assertVisible(UiElement.Text("Database"))
    //     }
    //
    //     return FlowsItemDto(
    //         uid = PROJECT_ID,
    //         project = PROJECT_NAME,
    //         name = "Unlock screen",
    //         steps = steps
    //     )
    // }
}