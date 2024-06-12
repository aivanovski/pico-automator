package com.github.aivanovski.picoautomator.domain.entity

import java.lang.Error

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

    fun <E> toLeft(): Left<E> {
        return Left(unwrapError() as E)
    }

    fun <V> mapWith(value: V): Either<Error, V> {
        return if (isLeft()) {
            Left(unwrapError())
        } else {
            Right(value)
        }
    }

    fun <NewValue> map(transform: (Value) -> NewValue): Either<Error, NewValue> {
        return if (isRight()) {
            Right(transform(unwrap()))
        } else {
            Left(unwrapError())
        }
    }

    fun <NewError> mapError(transform: (Error) -> NewError): Either<NewError, Value> {
        return if (isLeft()) {
            Left(transform(unwrapError()))
        } else {
            Right(unwrap())
        }
    }

    companion object {

        fun <Error> left(error: Error): Left<Error> {
            return Left(error)
        }

        fun <Value> right(value: Value): Right<Value> {
            return Right(value)
        }
    }
}