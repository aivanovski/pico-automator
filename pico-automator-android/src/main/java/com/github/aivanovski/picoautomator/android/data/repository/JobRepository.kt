package com.github.aivanovski.picoautomator.android.data.repository

import com.github.aivanovski.picoautomator.android.data.db.dao.JobDao
import com.github.aivanovski.picoautomator.android.entity.db.JobEntry
import com.github.aivanovski.picoautomator.android.entity.exception.AppException
import com.github.aivanovski.picoautomator.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.picoautomator.domain.entity.Either

class JobRepository(
    private val dao: JobDao
) {

    fun add(entry: JobEntry) {
        dao.insert(entry)
    }

    fun getAll(): List<JobEntry> {
        return dao.getAll()
            .sortedByDescending { entry -> entry.addedTimestamp }
    }

    fun getJobByUid(uid: String): Either<AppException, JobEntry> {
        val entry = dao.getByUid(uid)
            ?: return Either.Left(newFailedToFindEntityError(uid))

        return Either.Right(entry)
    }

    fun update(entry: JobEntry): Either<AppException, Unit> {
        val getEntryResult = getJobByUid(entry.uid)
        if (getEntryResult.isLeft()) {
            return getEntryResult.toLeft()
        }

        val existingEntry = getEntryResult.unwrap()
        dao.update(
            entry.copy(
                id = existingEntry.id
            )
        )

        return Either.Right(Unit)
    }

    fun removeByUid(uid: String): Either<AppException, Unit> {
        dao.removeByUid(uid)
        return Either.Right(Unit)
    }

    private fun newFailedToFindEntityError(
        uid: String
    ): FailedToFindEntityException {
        return FailedToFindEntityException(
            entityName = JobEntry::class.java.simpleName,
            entityField = "uid",
            fieldValue = uid
        )
    }
}