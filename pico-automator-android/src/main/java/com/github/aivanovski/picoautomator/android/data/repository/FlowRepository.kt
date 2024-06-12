package com.github.aivanovski.picoautomator.android.data.repository

import com.github.aivanovski.picoautomator.android.entity.db.FlowEntry
import com.github.aivanovski.picoautomator.android.entity.db.StepEntry
import com.github.aivanovski.picoautomator.android.entity.FlowWithSteps
import com.github.aivanovski.picoautomator.android.entity.exception.AppException
import com.github.aivanovski.picoautomator.domain.entity.Either

interface FlowRepository {

    suspend fun findStepByUid(uid: String): Either<AppException, StepEntry?>

    suspend fun getFlowByUid(
        flowUid: String
    ): Either<AppException, FlowWithSteps>

    suspend fun getStepByUid(
        stepUid: String
    ): Either<AppException, StepEntry>

    suspend fun removeFlowData(flowUid: String): Either<AppException, Unit>

    suspend fun save(flow: FlowWithSteps): Either<AppException, Unit>

    suspend fun updateFlow(
        flowEntry: FlowEntry
    ): Either<AppException, Unit>

    suspend fun updateStep(
        stepEntry: StepEntry
    ): Either<AppException, Unit>

    suspend fun getNextStep(
        stepUid: String?
    ): Either<AppException, StepEntry?>
}