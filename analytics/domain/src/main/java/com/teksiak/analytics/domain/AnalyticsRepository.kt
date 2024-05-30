package com.teksiak.analytics.domain

interface AnalyticsRepository {
    suspend fun getAnalyticsData(): AnalyticsData

}