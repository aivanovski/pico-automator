package com.github.aivanovski.picoautomator.web.data.repository

import com.github.aivanovski.picoautomator.web.entity.User
import com.github.aivanovski.picoautomator.web.entity.exception.AppException
import com.github.aivanovski.picoautomator.web.entity.exception.EntityNotFoundException
import com.github.aivanovski.picoautomator.domain.entity.Either

class FakeUserRepository : UserRepository {

    override fun getUserUid(uid: String): Either<AppException, User> {
        if (uid == ADMIN.uid) {
            return Either.Right(ADMIN)
        }

        return Either.Left(
            EntityNotFoundException(
                entity = User::class.java.simpleName,
                key = "uid",
                value = uid
            )
        )
    }

    override fun getUserByName(name: String): Either<AppException, User> {
        if (name == ADMIN.name) {
            return Either.Right(ADMIN)
        }

        return Either.Left(
            EntityNotFoundException(
                entity = User::class.java.simpleName,
                key = "name",
                value = name
            )
        )
    }

    companion object {
        private val ADMIN = User(
            uid = "uid/admin",
            name = "admin"
        )
    }
}