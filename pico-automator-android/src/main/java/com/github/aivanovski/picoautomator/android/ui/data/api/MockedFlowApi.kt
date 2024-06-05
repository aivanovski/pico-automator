package com.github.aivanovski.picoautomator.android.ui.data.api

import com.github.aivanovski.picoautomator.android.ui.data.api.FlowDsl.createFlow
import com.github.aivanovski.picoautomator.android.ui.data.api.entity.FlowStep
import com.github.aivanovski.picoautomator.android.ui.data.db.dao.StepInfoDao
import com.github.aivanovski.picoautomator.android.ui.data.entity.StepAction
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.ElementReference
import java.util.concurrent.atomic.AtomicReference

class MockedFlowApi(
    private val dao: StepInfoDao
) : FlowApi {

    var currentUid = AtomicReference<String>()
    private val flow = buildFlow()

    override fun getFirstStep(): Either<Exception, FlowStep> {
        return Either.Right(flow.first())
    }

    override fun getNextStep(): Either<Exception, FlowStep?> {
        val currentUid = currentUid.get() ?: return getFirstStep()

        val stepIdx = flow.indexOfFirst { step -> step.uid == currentUid }
        if (stepIdx == -1) {
            return Either.Left(IllegalStateException("Invalid uid: $currentUid"))
        }

        return if (stepIdx == flow.lastIndex) {
            Either.Right(null)
        } else {
            Either.Right(flow[stepIdx + 1])
        }
    }

    override fun onStepComplete(
        step: FlowStep,
        isSuccess: Boolean
    ): Either<Exception, StepAction> {
        val uid = step.uid

        val stepIdx = flow.indexOfFirst { s -> s.uid == uid }
        if (stepIdx == -1) {
            return Either.Left(IllegalStateException("Invalid uid: $uid"))
        }

        if (!isSuccess) {
            val retryCount = dao.getAll()
                .firstOrNull { s -> s.uid == uid }
                ?.attemptCount
                ?: 0

            return if (retryCount < 3) {
                Either.Right(StepAction.RETRY)
            } else {
                Either.Right(StepAction.STOP)
            }
        }

        currentUid.set(flow[stepIdx].uid)

        return if (stepIdx == flow.lastIndex) {
            Either.Right(StepAction.COMPLETE)
        } else {
            Either.Right(StepAction.NEXT)
        }
    }

    companion object {
        private const val TEST_DATA_RECEIVER_ACITON =
            "com.ivanovsky.passnotes.domain.test.TestDataBroadcastReceiver"

        private fun buildFlow(): List<FlowStep> {
            return createFlow(
                packageName = "com.ivanovsky.passnotes",
                uid = "passnotes/login"
            ) {
                sendBroadcast(
                    action = TEST_DATA_RECEIVER_ACITON,
                    data = mapOf("isResetAppData" to "true")
                )
                sendBroadcast(
                    action = TEST_DATA_RECEIVER_ACITON,
                    data = mapOf("fakeFileName" to "passwords.kdbx")
                )
                launch()
                assertVisible(ElementReference.text("passwords.kdbx"))
                tapOn(ElementReference.text("Password"))
                inputText("abc123")
                tapOn(ElementReference.contentDesc("unlockButton"))
                assertVisible(ElementReference.text("DISABLE"))
                tapOn(ElementReference.text("DISABLE"))
                assertVisible(ElementReference.text("Database"))
            }
        }
    }
}