package com.teksiak.core.connectivity.domain.messaging

import com.teksiak.core.domain.location.Location
import kotlin.time.Duration

sealed interface MessagingAction {
    data object StartOrResume: MessagingAction
    data object Pause: MessagingAction
    data object Finish: MessagingAction
    data object Trackable: MessagingAction
    data object Untrackable: MessagingAction
    data object ConnectionRequest: MessagingAction
    data class HeartRateUpdate(val heartRate: Int): MessagingAction
    data class DistanceUpdate(val distanceMeters: Int): MessagingAction
    data class TimeUpdate(val elapsedDuration: Duration): MessagingAction
    data class LocationUpdate(val location: Location): MessagingAction
}