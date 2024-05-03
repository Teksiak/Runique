package com.teksiak.core.data.di

import com.teksiak.core.data.auth.EncryptedSessionStorage
import com.teksiak.core.data.networking.HttpClientFactory
import com.teksiak.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(
            sessionStorage = get(),
        ).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
}