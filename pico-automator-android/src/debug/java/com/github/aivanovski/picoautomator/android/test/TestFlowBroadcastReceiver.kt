package com.github.aivanovski.picoautomator.android.test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.aivanovski.picoautomator.android.di.GlobalInjector
import com.github.aivanovski.picoautomator.android.domain.FlowInteractor
import com.github.aivanovski.picoautomator.android.utils.Base64Utils
import com.github.aivanovski.picoautomator.android.utils.StringUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class TestFlowBroadcastReceiver : BroadcastReceiver() {

    private val flowInteractor: FlowInteractor by GlobalInjector.inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        val data = intent?.extras?.getString(EXTRA_TEST_FLOW)

        Timber.d("onReceive: data.size=%s", data?.length)
        if (data != null) {
            printFlow(data)

            scope.launch {
                val addResult = flowInteractor.parseAndAddToJobQueue(data)
                Timber.d(
                    "Prepare result: isSuccess=%s, result=%s",
                    addResult.isRight(),
                    addResult
                )
                if (addResult.isLeft()) {
                    Timber.e(addResult.unwrapError())
                    return@launch
                }

                val jobId = addResult.unwrap()
                flowInteractor.removeAllJobs(excludeJobUids = setOf(jobId))
            }
        }
    }

    private fun printFlow(data: String) {
        val decodedData = Base64Utils.decode(data) ?: StringUtils.EMPTY

        val lines = decodedData.split("\n")
        Timber.d("Test flow: %s lines", lines.size)
        for (line in lines) {
            Timber.d(line)
        }
    }

    companion object {
        private const val EXTRA_TEST_FLOW = "testFlow"
    }
}