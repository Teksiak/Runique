package com.teksiak.analytics.data

import com.teksiak.analytics.domain.AnalyticsData
import com.teksiak.analytics.domain.AnalyticsRepository
import com.teksiak.core.database.dao.AnalyticsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class RoomAnalyticsRepository(
    private val analyticsDao: AnalyticsDao
): AnalyticsRepository {

    override suspend fun getAnalyticsData(): AnalyticsData {
        return withContext(Dispatchers.IO) {
            val totalDistance = async { analyticsDao.getTotalDistance() }
            val totalDuration = async { analyticsDao.getTotalDuration() }
            val maxSpeed = async { analyticsDao.getMaxSpeed() }
            val avgDistancePerRun = async { analyticsDao.getAvgDistancePerRun() }
            val avgPacePerRun = async { analyticsDao.getAvgPacePerRun() }

            AnalyticsData(
                totalDistance = totalDistance.await(),
                totalDuration = totalDuration.await().milliseconds,
                maxSpeed = maxSpeed.await(),
                avgDistancePerRun = avgDistancePerRun.await(),
                avgPacePerRun = avgPacePerRun.await()
            )
        }
    }
}