package com.github.aivanovski.picoautomator.android.ui

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import com.github.aivanovski.picoautomator.android.ui.data.Settings
import com.github.aivanovski.picoautomator.android.ui.di.GlobalInjector.inject
import com.github.aivanovski.picoautomator.android.ui.domain.FlowInteractor
import com.github.aivanovski.picoautomator.android.ui.domain.flow.driver.AccessibilityDriverImpl
import com.github.aivanovski.picoautomator.android.ui.domain.flow.FlowRunner
import com.github.aivanovski.picoautomator.android.ui.extensions.getFlags
import timber.log.Timber

class UiAccessibilityService : AccessibilityService() {

    private val settings: Settings by inject()
    private val interactor: FlowInteractor by inject()

    private val driver = AccessibilityDriverImpl(this, this)
    private val runner = FlowRunner(settings, interactor, driver)
    private var serviceConnection: ServiceConnection? = null
//    private val job = Job()
//    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand:")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate:")

        val serviceConnection = object : ServiceConnection {

            private var service: NotificationService.LocalBinder? = null

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Timber.d("onServiceConnected: ")
                this.service = (service as NotificationService.LocalBinder)
                    .apply {
                        connect()
                    }

                startTestIfNeed()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Timber.d("onServiceDisconnected: ")
                // service?.disconnect()

                service?.connect()
                stopTestIfNeed()
            }
        }

        val intent = NotificationService.newConnectIntent(this)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        this.serviceConnection = serviceConnection
    }

    private fun startTestIfNeed() {
        val state = settings.testRunnerState

        Timber.d("startTestIfNeed: state=$state")

        if (state != STATE_RUNNING) {
            return
        }

        if (runner.isFailed()) {
            runner.reset()
        }

        if (!runner.isRunning()) {
            runner.start()
        }
    }

    private fun stopTestIfNeed() {
        runner.stop()
    }

    // private fun start(service: ForegroundService.LocalBinder) {
    //     Timber.d("Starting flow:")
    //
    //     scope.launch {
    //         var steps = 0
    //
    //         while (service.getNextCommand() != null) {
    //             val nextCommand = service.getNextCommand()
    //             Timber.d("nextCommand: $nextCommand")
    //
    //             if (nextCommand != null) {
    //                 val result = runner.run(nextCommand, driver)
    //                 service.onCommandResult(nextCommand, result)
    //             }
    //
    //             steps++
    //             if (steps > 10) {
    //                 break
    //             }
    //         }
    //     }
    // }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy:")
        // state.set(STATE_STOPPED)
//        job.cancel()
        runner.stop()

        serviceConnection?.let { unbindService(it) }
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val state = settings.testRunnerState
        // Timber.d("onAccessibilityEvent: state=$state")

        if (state != STATE_RUNNING) {
            return
        }

        // if (state == STATE_RUNNING && runner.isFailed()) {
        //     runner.reset()
        //     return
        // }

        val eventType = event?.eventType ?: 0
        val contentChangeType = event?.contentChangeTypes ?: 0
        val node = rootInActiveWindow

        Timber.d(
            "onAccessibilityEvent: type=%s, typeFlags=%s, packageName=%s, changeType=%s, changeTypeFlags=%s, node=",
            eventType,
            eventType.getFlags(TYPE_FLAGS),
            event?.packageName,
            contentChangeType,
            contentChangeType.getFlags(CONTENT_CHANGE_TYPE_FLAGS),
            if (node == null) "null" else node.className
        )

        // if (node != null) {
        //     driver.onUiTreeUpdated(node)
        // }

        // if (!runner.isRunning() && !runner.isFailed()) {
        //     runner.start()
        // }
    }

    companion object {

        private val TYPE_FLAGS = mapOf(
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED to "TYPE_WINDOW_CONTENT_CHANGED",
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED to "TYPE_WINDOW_STATE_CHANGED",
            AccessibilityEvent.TYPE_WINDOWS_CHANGED to "TYPE_WINDOWS_CHANGED",
        )

        private val CONTENT_CHANGE_TYPE_FLAGS = mapOf(
            AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION to "CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION",
            AccessibilityEvent.CONTENT_CHANGE_TYPE_STATE_DESCRIPTION to "CONTENT_CHANGE_TYPE_STATE_DESCRIPTION",
            AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE to "CONTENT_CHANGE_TYPE_STATE_DESCRIPTION",
            AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT to "CONTENT_CHANGE_TYPE_TEXT",
            AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_TITLE to "CONTENT_CHANGE_TYPE_PANE_TITLE",
            AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED to "CONTENT_CHANGE_TYPE_UNDEFINED",
            AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_APPEARED to "CONTENT_CHANGE_TYPE_PANE_APPEARED",
            AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED to "CONTENT_CHANGE_TYPE_PANE_APPEARED",
        )

        const val STATE_STOPPED = 0
        const val STATE_IDLE = 1
        const val STATE_RUNNING = 2
    }
}