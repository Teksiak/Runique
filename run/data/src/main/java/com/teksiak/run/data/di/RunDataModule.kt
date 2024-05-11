package com.teksiak.run.data.di

import com.teksiak.core.domain.run.RunSyncScheduler
import com.teksiak.run.data.CreateRunWorker
import com.teksiak.run.data.DeleteRunWorker
import com.teksiak.run.data.FetchRunsWorker
import com.teksiak.run.data.RunSyncWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

    singleOf(::RunSyncWorkerScheduler).bind<RunSyncScheduler>()
}