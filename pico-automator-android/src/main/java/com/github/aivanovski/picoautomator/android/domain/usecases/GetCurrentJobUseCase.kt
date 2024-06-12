package com.github.aivanovski.picoautomator.android.domain.usecases

import com.github.aivanovski.picoautomator.android.data.repository.JobRepository
import com.github.aivanovski.picoautomator.android.entity.JobStatus
import com.github.aivanovski.picoautomator.android.entity.db.JobEntry
import com.github.aivanovski.picoautomator.android.entity.exception.AppException
import com.github.aivanovski.picoautomator.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.picoautomator.domain.entity.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetCurrentJobUseCase(
    private val repository: JobRepository
) {

    suspend fun getCurrentJob(): Either<AppException, JobEntry?> =
        withContext(Dispatchers.IO) {
            val entry = repository.getAll().firstOrNull { entry ->
                entry.status == JobStatus.RUNNING
            }

            Either.Right(entry)
        }

    private fun newUnableToFindStartEntry(
        status: JobStatus
    ): FailedToFindEntityException {
        return FailedToFindEntityException(
            entityName = JobEntry::class.java.simpleName,
            entityField = "status",
            fieldValue = status.name
        )
    }
}