package com.teksiak.run.domain

import com.teksiak.core.connectivity.domain.DeviceNode
import com.teksiak.core.connectivity.domain.messaging.MessagingAction
import com.teksiak.core.connectivity.domain.messaging.MessagingError
import com.teksiak.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WatchConnector {
    val connectedDevice: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessagingAction>

    suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError>
    fun setIsTrackable(isTrackable: Boolean)
}