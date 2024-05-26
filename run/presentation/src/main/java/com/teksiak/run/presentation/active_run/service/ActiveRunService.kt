package com.teksiak.run.presentation.active_run.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.teksiak.core.presentation.ui.formatted
import com.teksiak.run.domain.RunningTracker
import com.teksiak.run.presentation.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class ActiveRunService: Service() {

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private val activeRunNotification by lazy {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSmallIcon(com.teksiak.core.presentation.designsystem.R.drawable.run)
            .setContentTitle(getString(R.string.active_run))
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
    }

    private val runningTracker by inject<RunningTracker>()

    private val broadcastReceiver = ActiveRunBroadcastReceiver(runningTracker)

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> {
                val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_CLASS)
                require(activityClass != null)

                start(Class.forName(activityClass))
            }
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun start(activityClass: Class<*>) {
        if(!isServiceActive) {
            isServiceActive = true
            createNotificationChannel()

            val activityIntent = Intent(applicationContext, activityClass).apply {
                data = "runique://active_run".toUri()
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }

            val notification = activeRunNotification
                .setContentText("00:00:00")
                .setContentIntent(pendingIntent)
                .build()

            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    FOREGROUND_SERVICE_TYPE_LOCATION
                } else {
                    0
                }
            )

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    broadcastReceiver,
                    IntentFilter().apply {
                        addAction(ActiveRunBroadcastReceiver.ACTION_PAUSE)
                        addAction(ActiveRunBroadcastReceiver.ACTION_RESUME)
                    },
                    RECEIVER_EXPORTED,
                )
            } else {
                registerReceiver(
                    broadcastReceiver,
                    IntentFilter().apply {
                        addAction(ActiveRunBroadcastReceiver.ACTION_PAUSE)
                        addAction(ActiveRunBroadcastReceiver.ACTION_RESUME)
                    }
                )
            }

            updateNotification()
        }
    }

    private fun stop() {
        stopSelf()
        unregisterReceiver(broadcastReceiver)
        isServiceActive = false
        serviceScope.cancel()
        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    private fun updateNotification() {
        combine(runningTracker.elapsedTime, runningTracker.isTracking) { elapsedTime, isTracking ->
            val content = if (isTracking) {
                elapsedTime.formatted()
            } else {
                elapsedTime.formatted() + getString(R.string.paused)
            }

            activeRunNotification
                .setContentText(content)
                .clearActions()
                .addAction(
                    0,
                    if(isTracking) "Pause" else "Resume",
                    PendingIntent.getBroadcast(
                        applicationContext,
                        1,
                        Intent(if(isTracking) ActiveRunBroadcastReceiver.ACTION_PAUSE else ActiveRunBroadcastReceiver.ACTION_RESUME),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
                .build()
        }
        .onEach { notification ->
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
        .launchIn(serviceScope)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.active_run),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        var isServiceActive = false

        private const val CHANNEL_ID = "active_run"
        private const val NOTIFICATION_ID = 1

        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"

        private const val EXTRA_ACTIVITY_CLASS = "EXTRA_ACTIVITY_CLASS"

        fun createStartIntent(context: Context, activityClass: Class<*>): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVITY_CLASS, activityClass.name)
            }
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }
}