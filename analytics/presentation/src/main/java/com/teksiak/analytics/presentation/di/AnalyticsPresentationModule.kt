package com.teksiak.analytics.presentation.di

import com.teksiak.analytics.presentation.compare_run.CompareRunViewModel
import com.teksiak.analytics.presentation.dashboard.AnalyticsDashboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val analyticsPresentationModule = module {
    viewModelOf(::AnalyticsDashboardViewModel)
    viewModelOf(::CompareRunViewModel)
}