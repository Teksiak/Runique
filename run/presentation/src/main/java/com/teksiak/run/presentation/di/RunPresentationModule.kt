package com.teksiak.run.presentation.di

import com.teksiak.run.domain.RunningTracker
import com.teksiak.run.presentation.active_run.ActiveRunViewModel
import com.teksiak.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val runPresentationModel = module {
    singleOf(::RunningTracker)
    single(named("elapsedTime")) {
        get<RunningTracker>().elapsedTime
    }
    single(named("isTracking")) {
        get<RunningTracker>().isTracking
    }

    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}