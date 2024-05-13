package com.teksiak.core.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AnalyticsDao {

    @Query("SELECT SUM(distanceMeters) FROM Run")
    suspend fun getTotalDistance(): Int

    @Query("SELECT SUM(durationMillis) FROM Run")
    suspend fun getTotalDuration(): Long

    @Query("SELECT MAX(maxSpeedKmh) FROM Run")
    suspend fun getMaxSpeed(): Double

    @Query("SELECT AVG(distanceMeters) FROM Run")
    suspend fun getAvgDistancePerRun(): Double

    @Query("SELECT AVG((durationMillis / 60000.0) / (distanceMeters / 1000.0)) FROM Run")
    suspend fun getAvgPacePerRun(): Double
}