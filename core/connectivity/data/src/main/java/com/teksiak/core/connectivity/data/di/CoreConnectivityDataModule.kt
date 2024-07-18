package com.teksiak.core.connectivity.data.di

import com.teksiak.core.connectivity.data.WearNodeDiscovery
import com.teksiak.core.connectivity.data.messaging.WearMessagingClient
import com.teksiak.core.connectivity.domain.NodeDiscovery
import com.teksiak.core.connectivity.domain.messaging.MessagingClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreConnectivityDataModule = module {
    singleOf(::WearNodeDiscovery).bind<NodeDiscovery>()
    singleOf(::WearMessagingClient).bind<MessagingClient>()
}