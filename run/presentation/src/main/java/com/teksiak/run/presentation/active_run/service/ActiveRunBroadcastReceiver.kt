package com.teksiak.run.presentation.active_run.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.teksiak.run.domain.RunningTracker

class ActiveRunBroadcastReceiver(
    private val runningTracker: RunningTracker
): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_PAUSE) {
            runningTracker.setIsTracking(false)
        } else if (intent?.action == ACTION_RESUME) {
            runningTracker.setIsTracking(true)
        }
    }

    companion object {
        const val ACTION_PAUSE = "com.teksiak.run.PAUSE_RUN"
        const val ACTION_RESUME = "com.teksiak.run.RESUME_RUN"
    }

}