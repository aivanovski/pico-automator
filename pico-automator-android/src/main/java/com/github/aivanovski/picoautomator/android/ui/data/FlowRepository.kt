package com.github.aivanovski.picoautomator.android.ui.data

import com.github.aivanovski.picoautomator.android.ui.data.db.entity.StepInfo
import com.github.aivanovski.picoautomator.android.ui.data.entity.StepAction
import com.github.aivanovski.picoautomator.domain.entity.Either

interface FlowRepository {

    fun findStepByUid(uid: String): Either<Exception, StepInfo?>

    fun clear(): Either<Exception, Unit>

    fun getNextStep(): Either<Exception, StepInfo>

    fun onStepComplete(
        stepUid: String,
        result: Either<Exception, Any>
    ): Either<Exception, StepAction>
}