package com.github.aivanovski.picoautomator.web.data.repository

import com.github.aivanovski.picoautomator.web.entity.Flow
import com.github.aivanovski.picoautomator.web.entity.User
import com.github.aivanovski.picoautomator.web.entity.exception.AppException
import com.github.aivanovski.picoautomator.domain.entity.Either

class FakeFlowRepository : FlowRepository {

    override fun getFlows(user: User): Either<AppException, List<Flow>> {
        return Either.Right(FLOWS)
    }

    companion object {

        private val FLOWS = listOf(
            Flow(
                uid = "UID:com.ivanovsky.passnotes:unlock",
                projectUid = "UID:com.ivanovsky.passnotes",
                name = "Unlock database",
                resource = "tests/unlock.yaml"
            )
        )
    }
}