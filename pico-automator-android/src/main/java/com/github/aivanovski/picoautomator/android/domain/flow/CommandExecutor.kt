package com.github.aivanovski.picoautomator.android.domain.flow

import com.github.aivanovski.picoautomator.android.domain.FlowInteractor
import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.android.domain.flow.commands.CompositeStepCommand
import com.github.aivanovski.picoautomator.android.domain.flow.commands.ExecutableStepCommand
import com.github.aivanovski.picoautomator.android.domain.flow.commands.RunFlow
import com.github.aivanovski.picoautomator.android.entity.OnStepFinishedAction
import com.github.aivanovski.picoautomator.android.entity.db.FlowEntry
import com.github.aivanovski.picoautomator.android.entity.db.JobEntry
import com.github.aivanovski.picoautomator.android.entity.db.StepEntry
import com.github.aivanovski.picoautomator.android.entity.exception.AppException
import com.github.aivanovski.picoautomator.android.entity.exception.FlowException
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.steps.StepCommand
import kotlinx.coroutines.delay

class CommandExecutor(
    private val interactor: FlowInteractor,
    private val driver: Driver
) {

    suspend fun execute(
        job: JobEntry,
        flow: FlowEntry,
        stepEntry: StepEntry,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int,
        lifecycleListener: FlowLifecycleListener,
    ): Either<AppException, OnStepFinishedAction> {
        lifecycleListener.onStepStarted(flow, command, stepIndex, attemptIndex)

        val result = when {
            command is ExecutableStepCommand<*> -> {
                command.execute(driver).mapError { exception -> FlowException(exception) }
            }

            command is CompositeStepCommand -> executeCompositeCommand(
                job,
                command,
                lifecycleListener
            )

            else -> throw IllegalArgumentException() // TODO: migrate StepCommand to sealed class
        }

        val getNextActionResult = interactor.onStepFinished(job.uid, stepEntry, result)
        if (getNextActionResult.isLeft()) {
            return getNextActionResult.toLeft()
        }

        lifecycleListener.onStepFinished(flow, command, stepIndex, result)

        val nextAction = getNextActionResult.unwrap()
        if (nextAction is OnStepFinishedAction.Next) {
            interactor.updateJob(job.copy(currentStepUid = nextAction.nextStepUid))
        }

        return Either.Right(nextAction)
    }

    private suspend fun executeCompositeCommand(
        job: JobEntry,
        compositeCommand: CompositeStepCommand,
        lifecycleListener: FlowLifecycleListener
    ): Either<AppException, Any> {
        var lastResult: Either<AppException, Any>? = null

        if (compositeCommand !is RunFlow) {
            throw IllegalStateException() // TODO: check
        }

        val flow = compositeCommand.flow

        lifecycleListener.onFlowStarted(flow.entry)

        val commands = compositeCommand.getCommands()
        var commandIndex = 0
        while (commandIndex < commands.size) {
            delay(FlowRunner.DELAY_BETWEEN_STEPS)

            val command = commands[commandIndex]
            if (command !is ExecutableStepCommand<*>) {
                throw IllegalStateException() // TODO: check
            }

            val stepUid = flow.steps[commandIndex].uid
            val getStepResult = interactor.getStepByUid(stepUid)
            if (getStepResult.isLeft()) {
                lifecycleListener.onFlowFinished(flow.entry, getStepResult.toLeft())
                return getStepResult.toLeft()
            }

            val getExecutionDataResult = interactor.getExecutionData(
                jobUid = job.uid,
                flowUid = flow.entry.uid,
                stepUid = stepUid
            )
            if (getExecutionDataResult.isLeft()) {
                return getExecutionDataResult.toLeft()
            }

            val stepEntry = getStepResult.unwrap()
            val executionData = getExecutionDataResult.unwrap()

            lifecycleListener.onStepStarted(
                flow.entry,
                command,
                commandIndex,
                executionData.attemptCount
            )

            val result = command.execute(driver)
                .mapError { exception -> FlowException(exception) }

            val getNextActionResult = interactor.onStepFinished(job.uid, stepEntry, result)
            if (getNextActionResult.isLeft()) {
                lifecycleListener.onFlowFinished(flow.entry, getNextActionResult.toLeft())
                return getNextActionResult.toLeft()
            }

            lifecycleListener.onStepFinished(
                flow.entry,
                command,
                commandIndex,
                result
            )

            lastResult = result

            val nextAction = getNextActionResult.unwrap()
            when (nextAction) {
                is OnStepFinishedAction.Next -> {
                    commandIndex++
                }

                OnStepFinishedAction.Stop -> {
                    lifecycleListener.onFlowFinished(flow.entry, result)
                    return Either.Left(AppException("Child flow was sopped"))
                }

                OnStepFinishedAction.Complete -> {
                    lifecycleListener.onFlowFinished(flow.entry, result)
                    break
                }

                OnStepFinishedAction.Retry -> {
                }
            }
        }

        return lastResult ?: Either.Left(AppException("No child steps"))
    }
}