package com.github.aivanovski.picoautomator.android.domain.usecases

import com.github.aivanovski.picoautomator.android.entity.db.FlowEntry
import com.github.aivanovski.picoautomator.android.entity.db.StepEntry
import com.github.aivanovski.picoautomator.android.entity.FlowSourceType
import com.github.aivanovski.picoautomator.android.entity.FlowWithSteps
import com.github.aivanovski.picoautomator.android.entity.StepVerificationType
import com.github.aivanovski.picoautomator.android.entity.exception.ParsingException
import com.github.aivanovski.picoautomator.android.utils.Base64Utils
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.yaml.YamlParser

class ParseFlowFileUseCase {

    fun parseBase64File(
        base64content: String
    ): Either<ParsingException, FlowWithSteps> {
        val decodedContent = Base64Utils.decode(base64content)
            ?: return Either.Left(ParsingException("Invalid bas64 string"))

        return parse(decodedContent)
    }

    private fun parse(
        content: String,
    ): Either<ParsingException, FlowWithSteps> {
        val parseFlowResult = YamlParser().parse(content)
        if (parseFlowResult.isLeft()) {
            return parseFlowResult.toLeft()
        }

        val flow = parseFlowResult.unwrap()
        val flowUid = flow.name
        val convertedSteps = flow.steps

        val steps = mutableListOf<StepEntry>()
        for (stepIdx in convertedSteps.indices) {
            val step = convertedSteps[stepIdx]
            val nextStep = convertedSteps.getOrNull(stepIdx + 1)
            val stepUid = "$flowUid:$stepIdx"

            val nextUid = if (nextStep != null) {
                "$flowUid:${stepIdx + 1}"
            } else {
                null
            }

            steps.add(
                StepEntry(
                    id = null,
                    uid = stepUid,
                    index = stepIdx,
                    flowUid = flowUid,
                    nextUid = nextUid,
                    command = step,
                    stepVerificationType = StepVerificationType.LOCAL
                )
            )
        }


        return Either.Right(
            FlowWithSteps(
                entry = FlowEntry(
                    id = null,
                    uid = flowUid,
                    name = flowUid,
                    sourceType = FlowSourceType.REMOTE
                ),
                steps = steps
            )
        )
    }
}