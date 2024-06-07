package com.github.aivanovski.picoautomator.web.data.repository

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.web.entity.Project
import com.github.aivanovski.picoautomator.web.entity.exception.AppException

class FakeProjectRepository : ProjectRepository {
    override fun getProjects(): Either<AppException, List<Project>> {
        return Either.Right(listOf(KEEPASS_VAULT_PROJECT))
    }

    companion object {
        val KEEPASS_VAULT_PROJECT = Project(
            uid = "UID:com.ivanovsky.passnotes",
            name = "KeePassVault"
        )
    }
}