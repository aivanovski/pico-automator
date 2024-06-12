package com.github.aivanovski.picoautomator.android.data.repository

import com.github.aivanovski.picoautomator.android.data.api.ApiClient
import com.github.aivanovski.picoautomator.android.data.db.dao.FlowEntryDao
import com.github.aivanovski.picoautomator.android.data.db.dao.StepEntryDao
import com.github.aivanovski.picoautomator.android.entity.db.FlowEntry
import com.github.aivanovski.picoautomator.android.entity.db.StepEntry
import com.github.aivanovski.picoautomator.android.domain.usecases.ParseFlowFileUseCase
import com.github.aivanovski.picoautomator.android.entity.FlowWithSteps
import com.github.aivanovski.picoautomator.android.entity.exception.AppException
import com.github.aivanovski.picoautomator.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.picoautomator.domain.entity.Either

class FlowRepositoryImpl(
    private val stepDao: StepEntryDao,
    private val flowDao: FlowEntryDao,
    private val api: ApiClient,
    private val parseFlowUseCase: ParseFlowFileUseCase
) : FlowRepository {

    override suspend fun findStepByUid(uid: String): Either<AppException, StepEntry?> {
        val step = stepDao.getAll()
            .firstOrNull { step -> step.uid == uid }

        return Either.Right(step)
    }

    override suspend fun getFlowByUid(
        flowUid: String
    ): Either<AppException, FlowWithSteps> {
        val flow = flowDao.getByUidWithSteps(flowUid)
            ?: return Either.Left(newUnableToFindFlowByUidError(flowUid))

        return Either.Right(flow)
    }

    override suspend fun getStepByUid(
        stepUid: String
    ): Either<AppException, StepEntry> {
        val step = stepDao.getByUid(stepUid)
            ?: return Either.Left(newUnableToFindStepByUidError(stepUid))

        return Either.Right(step)
    }

    override suspend fun removeFlowData(
        flowUid: String
    ): Either<AppException, Unit> {

        // TODO: make a transaction
        flowDao.removeByUid(flowUid)
        stepDao.removeByFlowUid(flowUid)

        return Either.Right(Unit)
    }

    override suspend fun save(
        flow: FlowWithSteps
    ): Either<AppException, Unit> {
        val flowUid = flow.entry.uid

        val removeResult = removeFlowData(flowUid)
        if (removeResult.isLeft()) {
            return removeResult.toLeft()
        }

        flowDao.insert(flow.entry)
        stepDao.insert(flow.steps)

        return Either.Right(Unit)
    }

    override suspend fun getNextStep(
        stepUid: String?
    ): Either<AppException, StepEntry?> {
        val flowUid = stepUid?.let { getFlowUidByStepUid(stepUid) }
        val existingFlowEntry = flowUid?.let { flowDao.getByUid(flowUid) }

        val flow = if (existingFlowEntry == null) {
            if (flowUid == null) {
                return Either.Left(AppException("Flow uid is null"))
            }

            val getFlowResult = api.getFlow(flowUid)
            if (getFlowResult.isLeft()) {
                return getFlowResult.toLeft()
            }

            val response = getFlowResult.unwrap()
            val parseResult = parseFlowUseCase.parseBase64File(
                base64content = response.flow.base64Content
            )
            if (parseResult.isLeft()) {
                return parseResult.toLeft()
            }

            val flowEntry = parseResult.unwrap()

            flowDao.insert(flowEntry.entry)
            stepDao.insert(flowEntry.steps)

            flowEntry
        } else {
            flowDao.getByUidWithSteps(existingFlowEntry.uid)
                ?: return Either.Left(newUnableToFindFlowByUidError(existingFlowEntry.uid))
        }

        val currentStepEntry = stepDao.getByUid(stepUid)
            ?: return Either.Left(newUnableToFindStepByUidError(stepUid))

        val nextStepUid = currentStepEntry.nextUid

        return if (nextStepUid != null) {
            val nextEntry = stepDao.getByUid(nextStepUid)
                ?: return Either.Left(newUnableToFindStepByUidError(nextStepUid))

            Either.Right(nextEntry)
        } else {
            Either.Right(null)
        }
    }

    private fun getFlowUidByStepUid(stepUid: String): String? {
        return stepDao.getByUid(stepUid)?.flowUid
    }

    override suspend fun updateStep(
        stepEntry: StepEntry
    ): Either<AppException, Unit> {
        val existingEntry = stepDao.getByUid(stepEntry.uid)
            ?: return Either.Left(newUnableToFindStepByUidError(stepEntry.uid))

        stepDao.update(stepEntry.copy(id = existingEntry.id))

        return Either.Right(Unit)
    }

    override suspend fun updateFlow(
        flowEntry: FlowEntry
    ): Either<AppException, Unit> {
        val existingEntry = flowDao.getByUid(flowEntry.uid)
            ?: return Either.Left(newUnableToFindFlowByUidError(flowEntry.uid))

        flowDao.update(flowEntry.copy(id = existingEntry.id))

        return Either.Right(Unit)
    }

    private fun newUnableToFindFlowByUidError(uid: String): AppException {
        return FailedToFindEntityException(
            entityName = FlowEntry::class.java.simpleName,
            entityField = "uid",
            fieldValue = uid
        )
    }

    private fun newUnableToFindStepByUidError(uid: String): AppException {
        return FailedToFindEntityException(
            entityName = StepEntry::class.java.simpleName,
            entityField = "uid",
            fieldValue = uid
        )
    }
}