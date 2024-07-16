package com.teksiak.wear.run.presentation

sealed interface TrackerEvent {
    data object RunFinished: TrackerEvent
}