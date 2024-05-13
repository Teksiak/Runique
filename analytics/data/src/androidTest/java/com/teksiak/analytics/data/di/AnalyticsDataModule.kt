package com.teksiak.analytics.data.di

import com.teksiak.analytics.data.RoomAnalyticsRepository
import com.teksiak.analytics.domain.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
}