package com.github.aivanovski.picoautomator.android.data.repository

import com.github.aivanovski.picoautomator.android.data.db.dao.ExecutionDataDao
import com.github.aivanovski.picoautomator.android.entity.db.ExecutionData
import com.github.aivanovski.picoautomator.android.entity.exception.AppException
import com.github.aivanovski.picoautomator.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.picoautomator.android.utils.StringUtils
import com.github.aivanovski.picoautomator.domain.entity.Either

class ExecutionDataRepository(
    private val dao: ExecutionDataDao
) {

    fun getOrCreate(
        jobUid: String,
        flowUid: String,
        stepUid: String
    ): Either<AppException, ExecutionData> {
        val entry = dao.get(jobUid, flowUid, stepUid)

        val result = if (entry == null) {
            val newEntry = ExecutionData(
                jobUid = jobUid,
                flowUid = flowUid,
                stepUid = stepUid,
                attemptCount = 0,
                result = null
            )

            dao.insert(newEntry)

            newEntry
        } else {
            entry
        }

        return Either.Right(result)
    }

    fun add(entry: ExecutionData) {
        dao.insert(entry)
    }

    fun update(entry: ExecutionData): Either<AppException, Unit> {
        val existingEntry = dao.get(entry.jobUid, entry.flowUid, entry.stepUid)
            ?: return Either.Left(newFailedToFindEntityError())

        dao.update(
            entry.copy(
                id = existingEntry.id
            )
        )

        return Either.Right(Unit)
    }

    private fun newFailedToFindEntityError(): FailedToFindEntityException {
        return FailedToFindEntityException(
            entityName = ExecutionData::class.java.simpleName,
            entityField = StringUtils.EMPTY,
            fieldValue = StringUtils.EMPTY
        )
    }
}