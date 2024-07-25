package com.teksiak.analytics.data

import com.teksiak.analytics.domain.AnalyticsData
import com.teksiak.analytics.domain.AnalyticsGraphData
import com.teksiak.analytics.domain.AnalyticsRepository
import com.teksiak.core.database.dao.AnalyticsDao
import com.teksiak.core.domain.run.LocalRunDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class RoomAnalyticsRepository(
    private val analyticsDao: AnalyticsDao,
    private val localRunDataSource: LocalRunDataSource
): AnalyticsRepository {

    override suspend fun getAnalyticsData(): AnalyticsData {
        return withContext(Dispatchers.IO) {
            val totalDistance = async { analyticsDao.getTotalDistance() }
            val totalDuration = async { analyticsDao.getTotalDuration() }
            val maxSpeed = async { analyticsDao.getMaxSpeed() }
            val maxHeartRate = async { analyticsDao.getMaxHeartRate() }
            val runs = async { localRunDataSource.getUnsortedRuns() }

            AnalyticsData(
                totalDistance = totalDistance.await(),
                totalDuration = totalDuration.await().milliseconds,
                maxSpeed = maxSpeed.await(),
                maxHeartRate = maxHeartRate.await(),
                graphData = AnalyticsGraphData(runs.await())
            )
        }
    }

}