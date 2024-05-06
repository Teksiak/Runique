@file:OptIn(ExperimentalCoroutinesApi::class)

package com.teksiak.run.domain

import com.teksiak.core.domain.Timer
import com.teksiak.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {

    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    private val isTracking = MutableStateFlow(false)
    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()


    val currentLocation = isObservingLocation
        .flatMapLatest { isObservingLocation ->
            if(isObservingLocation) {
                locationObserver.observeLocation(500)
            } else flowOf()
        }
        .stateIn(applicationScope, SharingStarted.WhileSubscribed(), null)

    init {
        isTracking
            .onEach {isTracking ->
                if(!isTracking) {
                    val newList = buildList {
                        addAll(_runData.value.locations)
                        add(emptyList<LocationTimestamp>())
                    }.toList()
                    _runData.update {
                        it.copy(locations = newList)
                    }
                }
            }
            .flatMapLatest { isTracking ->
                if(isTracking) {
                    Timer.timeAndEmit()
                } else flowOf()
            }
            .onEach { duration ->
                _elapsedTime.update { it + duration }
            }
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(isTracking) { location, isTracking ->
                if(isTracking) {
                    emit(location)
                }
            }
            .zip(_elapsedTime) { location, elapsedTime ->
                LocationTimestamp(
                    location = location,
                    durationTimestamp = elapsedTime
                )
            }
            .onEach { location ->
                val currentLocations = _runData.value.locations
                val lastLocationsList = if(currentLocations.isNotEmpty()) {
                    currentLocations.last() + location
                } else listOf(location)
                val newLocationsList = currentLocations.replaceLast(lastLocationsList)

                val distanceMeters = LocationDataCalculator.getTotalDistanceMeters(newLocationsList)
                val distanceKm = distanceMeters / 1000.0
                val currentDuration = location.durationTimestamp

                val avgSecondsPerKm = if(distanceKm > 0) {
                    (currentDuration.inWholeSeconds / distanceKm).roundToInt()
                } else 0

                _runData.update {
                    RunData(
                        distanceMeters = distanceMeters,
                        pace = avgSecondsPerKm.seconds,
                        locations = newLocationsList,
                    )
                }
            }
            .launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        this.isTracking.update { isTracking }
    }

    fun startObservingLocation() {
        isObservingLocation.update { true }
    }

    fun stopObservingLocation() {
        isObservingLocation.update { false }
    }
}

private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    return if(isEmpty()) {
        listOf(replacement)
    } else {
        dropLast(1) + listOf(replacement)
    }
}