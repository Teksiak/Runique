package com.teksiak.core.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.teksiak.core.presentation.ui.formatted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import kotlin.time.Duration

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

    private val elapsedTime by inject<StateFlow<Duration>>(qualifier = named("elapsedTime"))

    private val isTracking by inject<StateFlow<Boolean>>(qualifier = named("isTracking"))

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
        if(!_isServiceActive.value) {
            _isServiceActive.value = true
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
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) FOREGROUND_SERVICE_TYPE_LOCATION else 0
            )

            updateNotification()
        }
    }

    private fun stop() {
        stopSelf()
        _isServiceActive.value = false
        serviceScope.cancel()
        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    private fun updateNotification() {
        combine(elapsedTime, isTracking) { elapsedTime, isTracking ->
            val content = if (isTracking) {
                elapsedTime.formatted()
            } else {
                elapsedTime.formatted() + getString(R.string.paused)
            }

            val notification = activeRunNotification
                .setContentText(content)
                .build()

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
        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive = _isServiceActive.asStateFlow()

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