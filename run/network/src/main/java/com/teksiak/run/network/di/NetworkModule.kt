package com.teksiak.run.network.di

import com.teksiak.core.domain.run.RemoteRunDataSource
import com.teksiak.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}