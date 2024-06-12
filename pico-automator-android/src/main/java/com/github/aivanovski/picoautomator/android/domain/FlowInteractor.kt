package com.github.aivanovski.picoautomator.android.domain

import com.github.aivanovski.picoautomator.android.data.repository.FlowRepository
import com.github.aivanovski.picoautomator.android.data.Settings
import com.github.aivanovski.picoautomator.android.data.repository.ExecutionDataRepository
import com.github.aivanovski.picoautomator.android.entity.db.StepEntry
import com.github.aivanovski.picoautomator.android.entity.OnStepFinishedAction
import com.github.aivanovski.picoautomator.android.data.repository.JobRepository
import com.github.aivanovski.picoautomator.android.domain.usecases.GetCurrentJobUseCase
import com.github.aivanovski.picoautomator.android.domain.usecases.ParseFlowFileUseCase
import com.github.aivanovski.picoautomator.android.entity.FlowSourceType
import com.github.aivanovski.picoautomator.android.entity.FlowWithSteps
import com.github.aivanovski.picoautomator.android.entity.JobData
import com.github.aivanovski.picoautomator.android.entity.OnFinishAction
import com.github.aivanovski.picoautomator.android.entity.JobStatus
import com.github.aivanovski.picoautomator.android.entity.StepVerificationType
import com.github.aivanovski.picoautomator.android.entity.db.ExecutionData
import com.github.aivanovski.picoautomator.android.entity.db.JobEntry
import com.github.aivanovski.picoautomator.android.entity.exception.AppException
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.FlowStep
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.AssertionException
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.FailedToGetUiNodesException
import com.github.aivanovski.picoautomator.domain.newapi.entity.exception.NodeException
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FlowInteractor(
    private val settings: Settings,
    private val flowRepository: FlowRepository,
    private val runnerRepository: JobRepository,
    private val executionRepository: ExecutionDataRepository,
    private val getCurrentJobUseCase: GetCurrentJobUseCase,
    private val parseFlowUseCase: ParseFlowFileUseCase
) {

    suspend fun getCurrentJobData(): Either<AppException, JobData> =
        withContext(Dispatchers.IO) {
            val getJobResult = getCurrentJobUseCase.getCurrentJob()
            if (getJobResult.isLeft()) {
                return@withContext getJobResult.toLeft()
            }

            // TODO: replace exception
            val job = getJobResult.unwrap()
                ?: return@withContext Either.Left(AppException("Unable to find current job"))

            val getFlowResult = getFlowByUid(job.flowUid)
            if (getFlowResult.isLeft()) {
                return@withContext getFlowResult.toLeft()
            }

            val flow = getFlowResult.unwrap()
            val step = flow.steps.firstOrNull { step -> step.uid == job.currentStepUid }
                ?: return@withContext Either.Left(
                    AppException("Unable to find step: ${job.currentStepUid}") // TODO: replace
                )

            val getExecutionDataResult = executionRepository.getOrCreate(
                jobUid = job.uid,
                flowUid = flow.entry.uid,
                stepUid = step.uid
            )
            if (getExecutionDataResult.isLeft()) {
                return@withContext getExecutionDataResult.toLeft()
            }

            val executionData = getExecutionDataResult.unwrap()

            Either.Right(
                JobData(
                    job = job,
                    flow = flow,
                    currentStep = step,
                    executionData = executionData
                )
            )
        }

    suspend fun getCurrentRunnerEntry(): Either<AppException, JobEntry?> =
        withContext(Dispatchers.IO) {
            getCurrentJobUseCase.getCurrentJob()
        }

    suspend fun getCurrentStepEntry(): Either<AppException, StepEntry?> =
        withContext(Dispatchers.IO) {
            val getRunningEntryResult = getCurrentJobUseCase.getCurrentJob()
            if (getRunningEntryResult.isLeft()) {
                return@withContext getRunningEntryResult.toLeft()
            }

            val runningEntry = getRunningEntryResult.unwrap()
                ?: return@withContext Either.Right(null)

            flowRepository.getStepByUid(runningEntry.currentStepUid)
        }

    suspend fun updateJob(entry: JobEntry): Either<AppException, Unit> =
        withContext(Dispatchers.IO) {
            runnerRepository.update(entry)
        }

    suspend fun removeJob(startId: String): Either<AppException, Unit> =
        withContext(Dispatchers.IO) {
            runnerRepository.removeByUid(startId)
        }

    suspend fun getJobs(): Either<Exception, List<JobEntry>> =
        withContext(Dispatchers.IO) {
            val entries = runnerRepository.getAll()
            Either.Right(entries)
        }

    suspend fun getFlowByUid(flowUid: String): Either<AppException, FlowWithSteps> =
        withContext(Dispatchers.IO) {
            flowRepository.getFlowByUid(flowUid)
        }

    suspend fun getStepByUid(stepUid: String): Either<AppException, StepEntry> =
        withContext(Dispatchers.IO) {
            flowRepository.getStepByUid(stepUid)
        }

    suspend fun getExecutionData(
        jobUid: String,
        flowUid: String,
        stepUid: String
    ): Either<AppException, ExecutionData> =
        withContext(Dispatchers.IO) {
            executionRepository.getOrCreate(jobUid, flowUid, stepUid)
        }

    suspend fun getJobByUid(
        jobUid: String
    ): Either<AppException, JobEntry> =
        withContext(Dispatchers.IO) {
            runnerRepository.getJobByUid(jobUid)
        }

    suspend fun removeAllJobs(
        excludeJobUids: Set<String>
    ): Either<AppException, Unit> =
        withContext(Dispatchers.IO) {
            val removeUids = runnerRepository.getAll()
                .filter { entry -> entry.uid !in excludeJobUids }
                .map { entry -> entry.uid }

            for (id in removeUids) {
                runnerRepository.removeByUid(id)
            }

            Either.Right(Unit)
        }

    suspend fun parseAndAddToJobQueue(
        base64Content: String
    ): Either<AppException, String> =
        withContext(Dispatchers.IO) {
            val parseResult = parseFlowUseCase.parseBase64File(base64Content)
            if (parseResult.isLeft()) {
                return@withContext parseResult.toLeft()
            }

            val flowUid = parseResult.unwrap().entry.uid
            val removeResult = flowRepository.removeFlowData(flowUid)
            if (removeResult.isLeft()) {
                return@withContext removeResult.toLeft()
            }

            val flow = parseResult.unwrap().let { flow ->
                flow.copy(
                    entry = flow.entry.copy(
                        sourceType = FlowSourceType.LOCAL
                    )
                )
            }
            val saveResult = flowRepository.save(flow)
            if (saveResult.isLeft()) {
                return@withContext saveResult.toLeft()
            }

            val firstStepUid = flow.steps.firstOrNull()?.uid
                ?: return@withContext Either.Left(AppException("No steps"))

            addRunnerEntry(
                flowUid = flowUid,
                stepUid = firstStepUid
            )
        }

    suspend fun parseAndAddToJobQueue(): Either<AppException, String> =
        withContext(Dispatchers.IO) {
            val removeResult = flowRepository.removeFlowData(FLOW_UID)
            if (removeResult.isLeft()) {
                return@withContext removeResult.toLeft()
            }

            val getNextStepResult = flowRepository.getNextStep(null)
            if (getNextStepResult.isLeft()) {
                return@withContext getNextStepResult.toLeft()
            }

            val firstStepUid = getNextStepResult.unwrap()?.uid
                ?: return@withContext Either.Left(AppException("No steps"))

            addRunnerEntry(
                flowUid = FLOW_UID,
                stepUid = firstStepUid
            )
        }

    suspend fun onStepFinished(
        jobUid: String,
        entry: StepEntry,
        result: Either<Exception, Any>
    ): Either<AppException, OnStepFinishedAction> =
        withContext(Dispatchers.IO) {
            when (entry.stepVerificationType) {
                StepVerificationType.LOCAL -> verifyLocally(jobUid, entry, result)
                StepVerificationType.REMOTE -> verifyRemotely(entry, result)
            }
        }

    private suspend fun verifyLocally(
        jobUid: String,
        stepEntry: StepEntry,
        result: Either<Exception, Any>
    ): Either<AppException, OnStepFinishedAction> {
        val getNextEntry = flowRepository.getNextStep(stepEntry.uid)
        if (getNextEntry.isLeft()) {
            return getNextEntry.toLeft()
        }

        val getFlowResult = flowRepository.getFlowByUid(flowUid = stepEntry.flowUid)
        if (getFlowResult.isLeft()) {
            return getFlowResult.toLeft()
        }

        val getExecutionData = executionRepository.getOrCreate(
            jobUid = jobUid,
            flowUid = stepEntry.flowUid,
            stepUid = stepEntry.uid
        )
        if (getExecutionData.isLeft()) {
            return getExecutionData.toLeft()
        }

        val executionData = getExecutionData.unwrap()
        val flow = getFlowResult.unwrap()
        val nextStepEntry = getNextEntry.unwrap()
        val isFinishedSuccessfully = result.isRight()
        val isLast = (nextStepEntry == null)
        val updatedExecutionData = executionData.copy(
            result = result.toString(),
            attemptCount = executionData.attemptCount + 1
        )
        val isRetry = canRetry(stepEntry, updatedExecutionData, result)

        val updateDataResult = executionRepository.update(updatedExecutionData)
        if (updateDataResult.isLeft()) {
            return updateDataResult.toLeft()
        }

        val action = if (isFinishedSuccessfully) {
            if (isLast) {
                OnStepFinishedAction.Complete
            } else {
                if (nextStepEntry != null) {
                    OnStepFinishedAction.Next(nextStepUid = nextStepEntry.uid)
                } else {
                    OnStepFinishedAction.Stop
                }
            }
        } else {
            if (isRetry) {
                OnStepFinishedAction.Retry
            } else {
                OnStepFinishedAction.Stop
            }
        }

        return Either.Right(action)
    }

    private fun verifyRemotely(
        entry: StepEntry,
        result: Either<Exception, Any>
    ): Either<AppException, OnStepFinishedAction> {
        TODO()
    }

    private suspend fun addRunnerEntry(
        flowUid: String,
        stepUid: String
    ): Either<AppException, String> =
        withContext(Dispatchers.IO) {
            val uid = UUID.randomUUID().toString()

            runnerRepository.add(
                JobEntry(
                    id = null,
                    flowUid = flowUid,
                    currentStepUid = stepUid,
                    uid = uid,
                    addedTimestamp = System.currentTimeMillis(),
                    status = JobStatus.PENDING,
                    onFinishAction = OnFinishAction.STOP
                )
            )

            settings.startJobUid = uid

            Either.Right(uid)
        }

    private fun canRetry(
        entry: StepEntry,
        executionData: ExecutionData,
        result: Either<Exception, Any>
    ): Boolean {
        if (result.isRight()) {
            return false
        }

        val exception = result.unwrapError()
        val isFlaky = (entry.command.isStepFlaky() || exception.isFlakyException())

        return isFlaky && executionData.attemptCount < 3
    }

    private fun FlowStep.isStepFlaky(): Boolean {
        return this is FlowStep.AssertVisible ||
            this is FlowStep.AssertNotVisible ||
            this is FlowStep.TapOn
    }

    private fun Exception.isFlakyException(): Boolean {
        return this is NodeException ||
            this is FailedToGetUiNodesException ||
            this is AssertionException
    }

    companion object {
        const val FLOW_UID = "UID:com.ivanovsky.passnotes:unlock"
    }
}