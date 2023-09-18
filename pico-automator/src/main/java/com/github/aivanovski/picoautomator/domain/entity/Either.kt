package com.github.aivanovski.picoautomator.domain.entity

sealed class Either<out Error : Any?, out Value : Any?> {

    data class Left<Error>(val error: Error) : Either<Error, Nothing>()
    data class Right<Value>(val value: Value) : Either<Nothing, Value>()

    fun isLeft() = (this is Left)

    fun isRight() = (this is Right)

    fun unwrap(): Value = (this as Right).value

    fun unwrapOrNull(): Value? {
        return if (isRight()) {
            unwrap()
        } else {
            null
        }
    }

    fun unwrapError(): Error = (this as Left).error

    fun <E> mapToLeft(): Left<E> {
        return Left(unwrapError() as E)
    }

    fun <V> mapWith(value: V): Either<Error, V> {
        return if (isLeft()) {
            Left(unwrapError())
        } else {
            Right(value)
        }
    }
}