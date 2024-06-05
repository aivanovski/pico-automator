package com.github.aivanovski.picoautomator.android.ui.data

import com.github.aivanovski.picoautomator.android.ui.data.api.FlowApi
import com.github.aivanovski.picoautomator.android.ui.data.api.MockedFlowApi
import com.github.aivanovski.picoautomator.android.ui.data.db.dao.StepInfoDao
import com.github.aivanovski.picoautomator.android.ui.data.db.entity.StepInfo
import com.github.aivanovski.picoautomator.android.ui.data.entity.StepAction
import com.github.aivanovski.picoautomator.domain.entity.Either

class FlowRepositoryImpl(
    private val dao: StepInfoDao,
    private val api: FlowApi
) : FlowRepository {

    override fun findStepByUid(uid: String): Either<Exception, StepInfo?> {
        val step = dao.getAll()
            .firstOrNull { step -> step.uid == uid }

        return Either.Right(step)
    }

    override fun clear(): Either<Exception, Unit> {
        dao.removeAll()
        (api as MockedFlowApi).currentUid.set(null)
        return Either.Right(Unit)
    }

    override fun getNextStep(): Either<Exception, StepInfo> {
        val getNextStepResult = api.getNextStep()
        if (getNextStepResult.isLeft()) {
            return getNextStepResult.mapToLeft()
        }

        val step = getNextStepResult.unwrap()
            ?: return Either.Left(IllegalStateException("No next step"))

        val stepEntry = StepInfo(
            uid = step.uid,
            nextUid = null,
            isFinished = false,
            command = step,
            result = null,
            attemptCount = 0
        )
        dao.insert(stepEntry)

        return Either.Right(stepEntry)
    }

    override fun onStepComplete(
        stepUid: String,
        result: Either<Exception, Any>
    ): Either<Exception, StepAction> {
        val isFinished = result.isRight()

        var stepEntry = dao.getAll()
            .firstOrNull { entry ->
                entry.uid == stepUid
            }
            ?: return Either.Left(
                IllegalStateException("Unable to find entity by uid: $stepUid")
            )

        stepEntry = stepEntry.copy(
            isFinished = isFinished,
            result = result.toString(),
            attemptCount = stepEntry.attemptCount + 1
        )

        dao.update(stepEntry)

        val sendStepResult = api.onStepComplete(stepEntry.command, isSuccess = isFinished)
        if (sendStepResult.isLeft()) {
            return sendStepResult.mapToLeft()
        }

        val nextAction = sendStepResult.unwrap()

        return when (nextAction) {
            StepAction.COMPLETE -> {
                Either.Right(StepAction.COMPLETE)
            }

            StepAction.NEXT -> {
                val getNextStepResult = api.getNextStep()
                if (getNextStepResult.isLeft()) {
                    return getNextStepResult.mapToLeft()
                }

                val nextStep = getNextStepResult.unwrap()
                    ?: return Either.Right(StepAction.COMPLETE)

                val nextStepEntry = StepInfo(
                    uid = nextStep.uid,
                    nextUid = null,
                    isFinished = false,
                    command = nextStep,
                    result = null,
                    attemptCount = 0
                )
                dao.removeByUid(nextStepEntry.uid)
                dao.insert(nextStepEntry)

                stepEntry = stepEntry.copy(
                    nextUid = nextStep.uid
                )
                dao.update(stepEntry)

                Either.Right(StepAction.NEXT)
            }

            StepAction.RETRY -> {
                Either.Right(StepAction.RETRY)
            }

            StepAction.STOP -> {
                Either.Right(StepAction.STOP)
            }
        }
    }
}