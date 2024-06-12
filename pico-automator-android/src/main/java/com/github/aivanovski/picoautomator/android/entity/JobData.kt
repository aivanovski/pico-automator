package com.github.aivanovski.picoautomator.android.entity

import com.github.aivanovski.picoautomator.android.entity.db.ExecutionData
import com.github.aivanovski.picoautomator.android.entity.db.JobEntry
import com.github.aivanovski.picoautomator.android.entity.db.StepEntry

data class JobData(
    val job: JobEntry,
    val flow: FlowWithSteps,
    val currentStep: StepEntry,
    val executionData: ExecutionData
)