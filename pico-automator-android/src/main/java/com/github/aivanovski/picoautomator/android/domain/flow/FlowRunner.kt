package com.github.aivanovski.picoautomator.android.domain.flow

import com.github.aivanovski.picoautomator.android.data.Settings
import com.github.aivanovski.picoautomator.android.entity.OnStepFinishedAction
import com.github.aivanovski.picoautomator.android.domain.FlowInteractor
import com.github.aivanovski.picoautomator.android.domain.flow.commands.Assert
import com.github.aivanovski.picoautomator.android.domain.flow.commands.Broadcast
import com.github.aivanovski.picoautomator.android.domain.flow.commands.InputText
import com.github.aivanovski.picoautomator.android.domain.flow.commands.Launch
import com.github.aivanovski.picoautomator.android.domain.flow.commands.PressKey
import com.github.aivanovski.picoautomator.android.domain.flow.commands.RunFlow
import com.github.aivanovski.picoautomator.android.domain.flow.commands.Tap
import com.github.aivanovski.picoautomator.android.domain.flow.commands.WaitUntil
import com.github.aivanovski.picoautomator.android.domain.flow.commands.assertion.NotVisibleAssertion
import com.github.aivanovski.picoautomator.android.domain.flow.commands.assertion.VisibleAssertion
import com.github.aivanovski.picoautomator.android.domain.flow.driver.Driver
import com.github.aivanovski.picoautomator.android.entity.OnFinishAction
import com.github.aivanovski.picoautomator.android.entity.JobStatus
import com.github.aivanovski.picoautomator.android.entity.db.FlowEntry
import com.github.aivanovski.picoautomator.android.entity.exception.AppException
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.FlowStep
import com.github.aivanovski.picoautomator.domain.steps.StepCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class FlowRunner(
    private val settings: Settings,
    private val interactor: FlowInteractor,
    private val driver: Driver
) {

    private val stateRef = AtomicReference(RunnerState.IDLE)
    private val stepIndex = AtomicInteger(0)
    private val jobUidRef = AtomicReference<String?>(null)
    private val listeners = mutableListOf<FlowLifecycleListener>()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main)
    private val commandExecutor = CommandExecutor(interactor, driver)

    init {
        listeners.add(TimberFlowReporter())
    }

    fun isRunning(): Boolean = (stateRef.get() == RunnerState.RUNNING)

    fun isIdle(): Boolean = (stateRef.get() == RunnerState.IDLE)

    fun startNextIfNeed() {
        val jobUid = settings.startJobUid

        Timber.d("startNextIfNeed: jobUid=%s", jobUid)

        scope.launch {
            val getJobsResult = interactor.getJobs()
            if (getJobsResult.isLeft()) {
                return@launch
            }

            val jobs = getJobsResult.unwrap()
            Timber.d("jobs: size=%s, %s", jobs.size, jobs)

            val running = jobs.filter { job ->
                job.status == JobStatus.RUNNING
            }
            val pending = jobs.filter { job ->
                job.status == JobStatus.PENDING
            }

            if (running.isNotEmpty() && isIdle()) {
                for (job in running) {
                    interactor.updateJob(
                        job.copy(
                            status = JobStatus.CANCELLED
                        )
                    )
                }
            }

            if (pending.isNotEmpty() && isIdle()) {
                val startJob = jobUid?.let {
                    pending.firstOrNull { entry ->
                        entry.uid == jobUid
                    }
                }

                val nextEntry = startJob ?: pending.first()

                start(nextEntry.uid)
            }
        }
    }

    private suspend fun start(jobUid: String) {
        val getJobsResult = interactor.getJobs()
        if (getJobsResult.isLeft()) {
            return
        }

        val jobs = getJobsResult.unwrap()
        val job = jobs.firstOrNull { job -> job.uid == jobUid }
            ?: return

        val getFlowResult = interactor.getFlowByUid(job.flowUid)
        if (getFlowResult.isLeft()) {
            return
        }

        val flow = getFlowResult.unwrap()
        jobUidRef.set(jobUid)
        stateRef.set(RunnerState.RUNNING)
        stepIndex.set(0)

        if (settings.startJobUid == jobUid) {
            settings.startJobUid = null
        }

        interactor.updateJob(job.copy(status = JobStatus.RUNNING))

        notifyOnFlowStarted(flow.entry)

        startNext(
            initialDelay = 5000L
        )
    }

    fun stop() {
        stateRef.set(RunnerState.IDLE)
        job.cancel()
    }

    private suspend fun onErrorOccurred(
        jobUid: String? = null,
        error: Either.Left<AppException>
    ) {
        Timber.e("onErrorOccurred: jobUid=%s, error=%s", jobUid, error)
        stateRef.set(RunnerState.IDLE)

        if (jobUid != null) {
            val job = interactor.getJobByUid(jobUid).unwrapOrNull()
            if (job != null) {
                interactor.updateJob(job.copy(status = JobStatus.CANCELLED))
            }
        }
    }

    private fun startNext(
        initialDelay: Long = DELAY_BETWEEN_STEPS
    ) {
        scope.launch {
            delay(initialDelay)

            if (!isRunning()) {
                Timber.d("Cancelled")
                return@launch
            }

            val getJobDataResult = interactor.getCurrentJobData()
            if (getJobDataResult.isLeft()) {
                onErrorOccurred(error = getJobDataResult.toLeft())
                return@launch
            }

            val (job, flow, stepEntry, executionData) = getJobDataResult.unwrap()

            val createCommandResult = createCommand(stepEntry.command)
            if (createCommandResult.isLeft()) {
                onErrorOccurred(job.uid, createCommandResult.toLeft())
                return@launch
            }

            val command = createCommandResult.unwrap()

            val result = commandExecutor.execute(
                job = job,
                flow = flow.entry,
                stepEntry = stepEntry,
                command = command,
                stepIndex = stepIndex.get(),
                attemptIndex = executionData.attemptCount,
                lifecycleListener = listeners.first()
            )

            val nextAction = result.unwrap()
            when (nextAction) {
                is OnStepFinishedAction.Next -> {
                    stepIndex.incrementAndGet()
                    startNext()
                }

                OnStepFinishedAction.Complete -> {
                    onFlowFinished(
                        jobUid = job.uid,
                        result = result
                    )
                }

                OnStepFinishedAction.Retry -> {
                    startNext()
                }

                OnStepFinishedAction.Stop -> {
                    onFlowFinished(
                        jobUid = job.uid,
                        result = result
                    )
                }
            }
        }
    }

    private suspend fun onFlowFinished(
        jobUid: String,
        result: Either<AppException, Any>
    ) {
        stateRef.set(RunnerState.IDLE)

        val job = interactor.getJobByUid(jobUid).unwrapOrNull() ?: return
        val flow = interactor.getFlowByUid(job.flowUid).unwrapOrNull() ?: return

        notifyOnFlowFinished(flow.entry, result)

        val isRunNext = (job.onFinishAction == OnFinishAction.RUN_NEXT)

        Timber.d(
            "onFlowFinished: onFinishAction=%s",
            job.onFinishAction
        )

        interactor.removeJob(jobUid)

        if (isRunNext) {
            startNextIfNeed()
        }
    }

    private suspend fun createCommand(step: FlowStep): Either<AppException, StepCommand> {
        return when (step) {
            is FlowStep.SendBroadcast -> Either.Right(
                Broadcast(
                    packageName = step.packageName,
                    action = step.action,
                    data = step.data
                )
            )

            is FlowStep.Launch -> Either.Right(
                Launch(
                    packageName = step.packageName,
                )
            )

            is FlowStep.AssertVisible -> Either.Right(
                Assert(
                    parent = null,
                    elements = step.elements,
                    assertion = VisibleAssertion()
                )
            )

            is FlowStep.AssertNotVisible -> Either.Right(
                Assert(
                    parent = null,
                    elements = step.elements,
                    assertion = NotVisibleAssertion()
                )
            )

            is FlowStep.TapOn -> Either.Right(
                Tap(
                    element = step.element,
                    isLongTap = step.isLong
                )
            )

            is FlowStep.InputText -> Either.Right(
                InputText(
                    text = step.text,
                    element = step.element
                )
            )

            is FlowStep.PressKey -> Either.Right(
                PressKey(
                    key = step.key
                )
            )

            is FlowStep.WaitUntil -> Either.Right(
                WaitUntil(
                    element = step.element,
                    step = step.step,
                    timeout = step.timeout
                )
            )

            is FlowStep.RunFlow -> {
                val flowUid = step.flowUid

                val getFlowResult = interactor.getFlowByUid(flowUid)
                if (getFlowResult.isLeft()) {
                    return getFlowResult.toLeft()
                }

                val flow = getFlowResult.unwrap()

                val commands = mutableListOf<StepCommand>()
                for (innerStep in flow.steps) {
                    val createCommandResult = createCommand(innerStep.command)
                    if (createCommandResult.isLeft()) {
                        return createCommandResult.toLeft()
                    }

                    commands.add(createCommandResult.unwrap())
                }

                Either.Right(
                    RunFlow(
                        flow = flow,
                        commands = commands
                    )
                )
            }
        }
    }

    private fun notifyOnFlowStarted(
        flow: FlowEntry
    ) {
        for (listener in listeners) {
            listener.onFlowStarted(flow)
        }
    }

    private fun notifyOnFlowFinished(
        flow: FlowEntry,
        result: Either<AppException, Any>
    ) {
        for (listener in listeners) {
            listener.onFlowFinished(flow, result)
        }
    }

    private fun notifyOnStepStarted(
        flow: FlowEntry,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    ) {
        for (listener in listeners) {
            listener.onStepStarted(flow, command, stepIndex, attemptIndex)
        }
    }

    private fun notifyOnStepFinished(
        flow: FlowEntry,
        command: StepCommand,
        stepIndex: Int,
        result: Either<AppException, Any>
    ) {
        for (listener in listeners) {
            listener.onStepFinished(flow, command, stepIndex, result)
        }
    }

    enum class RunnerState {
        IDLE,
        RUNNING
    }

    companion object {

        private const val DELAY_IF_PENDING_START = 5000L // in milliseconds
        const val DELAY_BETWEEN_STEPS = 1000L // in milliseconds
    }
}