package com.teksiak.run.data.di

import com.teksiak.run.data.CreateRunWorker
import com.teksiak.run.data.DeleteRunWorker
import com.teksiak.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)
}