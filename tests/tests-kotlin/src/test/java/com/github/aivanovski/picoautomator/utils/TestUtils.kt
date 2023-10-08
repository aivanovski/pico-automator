package com.github.aivanovski.picoautomator.utils

import com.github.aivanovski.picoautomator.domain.entity.Flow
import com.github.aivanovski.picoautomator.domain.runner.FlowRunner
import java.util.concurrent.CountDownLatch

object TestUtils {
    fun runAndCaptureOutput(flow: Flow): String {
        val latch = CountDownLatch(1)

        val reporter = CapturingTestFlowReporter(
            onFinished = {
                latch.countDown()
            }
        )

        FlowRunner()
            .apply {
                addLifecycleListener(reporter)
            }
            .run(flow)

        return reporter.getCapturedOutput()
    }
}