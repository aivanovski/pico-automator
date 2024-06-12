package com.github.aivanovski.picoautomator.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import com.github.aivanovski.picoautomator.android.driver.R
import kotlinx.parcelize.Parcelize
import timber.log.Timber

class NotificationService : Service() {

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder {
        Timber.d("onBind:")
        startForeground(NOTIFICATION_ID, createNotification("Service is running..."))
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("onUnbind:")
        stopForeground(STOP_FOREGROUND_REMOVE)
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate:")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val command = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.extras?.getParcelable(EXTRA_COMMAND, Command::class.java)
        } else {
            intent?.extras?.getParcelable(EXTRA_COMMAND)
        }

        requireNotNull(command)

        Timber.d("onStartCommand: command=$command")

        when (command) {
            Command.Start -> {
            }

            Command.Stop -> {
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy:")
    }

    private fun createNotification(message: String): Notification {
        val channel = NotificationChannel(
            getChannelId(),
            getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.notification_channel_description)
        }

        getNotificationManager().createNotificationChannel(channel)

        val actionIntent = PendingIntent.getService(
            this,
            0,
            newCommandIntent(this, Command.Stop),
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, getChannelId())
            .setContentTitle(getString(R.string.app_name))
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Stop",
                actionIntent
            )

        return builder.build()
    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun getChannelId(): String {
        return applicationContext.packageName + "_notification_channel"
    }

    private fun defaultPendingIntentFlags(): Int {
        return if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
    }

    inner class LocalBinder : Binder() {

        fun connect() {
        }

        fun disconnect() {
        }
    }

    sealed interface Command : Parcelable {

        @Parcelize
        object Stop : Command

        @Parcelize
        object Start : Command
    }

    companion object {
        private const val NOTIFICATION_ID = 101

        private const val EXTRA_COMMAND = "command"

        fun newConnectIntent(context: Context): Intent {
            return Intent(context, NotificationService::class.java)
        }

        fun newCommandIntent(
            context: Context,
            command: Command
        ): Intent {
            return Intent(context, NotificationService::class.java)
                .apply {
                    putExtra(EXTRA_COMMAND, command)
                }
        }
    }
}