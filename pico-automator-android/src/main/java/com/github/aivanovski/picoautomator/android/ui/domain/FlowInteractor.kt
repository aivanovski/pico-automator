package com.github.aivanovski.picoautomator.android.ui.domain

import com.github.aivanovski.picoautomator.android.ui.UiAccessibilityService
import com.github.aivanovski.picoautomator.android.ui.data.FlowRepository
import com.github.aivanovski.picoautomator.android.ui.data.Settings
import com.github.aivanovski.picoautomator.android.ui.data.db.entity.StepInfo
import com.github.aivanovski.picoautomator.android.ui.data.entity.StepAction
import com.github.aivanovski.picoautomator.domain.entity.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FlowInteractor(
    private val settings: Settings,
    private val repository: FlowRepository
) {

    suspend fun getCurrentStepInfo(): Either<Exception, StepInfo?> =
        withContext(Dispatchers.IO) {
            val currentUid = settings.currentStepUid
                ?: return@withContext Either.Right(null)

            repository.findStepByUid(currentUid)
        }

    suspend fun initFlow(): Either<Exception, Unit> =
        withContext(Dispatchers.IO) {
            val clearResult = repository.clear()
            if (clearResult.isLeft()) {
                return@withContext clearResult.mapToLeft()
            }

            val getNextStepResult = repository.getNextStep()
            if (getNextStepResult.isLeft()) {
                return@withContext getNextStepResult.mapToLeft()
            }

            val startUid = getNextStepResult.unwrap().uid

            settings.currentStepUid = startUid
            settings.testRunnerState = UiAccessibilityService.STATE_RUNNING

            Either.Right(Unit)
        }

    suspend fun onStepFinished(
        step: StepInfo,
        result: Either<Exception, Any>
    ): Either<Exception, StepAction> =
        withContext(Dispatchers.IO) {
            val onStepResult = repository.onStepComplete(step.uid, result)
            if (onStepResult.isLeft()) {
                return@withContext onStepResult.mapToLeft()
            }

            val nextAction = onStepResult.unwrap()

            when (nextAction) {
                StepAction.NEXT -> {
                    val getNextStepResult = repository.getNextStep()
                    if (getNextStepResult.isLeft()) {
                        return@withContext getNextStepResult.mapToLeft()
                    }

                    val nextEntry = getNextStepResult.unwrap()

                    settings.currentStepUid = nextEntry.uid
                }

                else -> {
                }
            }

            Either.Right(nextAction)
        }
}