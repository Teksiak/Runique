package com.teksiak.wear.app.presentation

import android.app.Application
import com.teksiak.core.connectivity.data.di.coreConnectivityDataModule
import com.teksiak.wear.app.presentation.di.appModule
import com.teksiak.wear.run.data.di.wearRunDataModule
import com.teksiak.wear.run.presentation.di.runPresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RuniqueApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                appModule,
                runPresentationModule,
                wearRunDataModule,
                coreConnectivityDataModule
            )
        }
    }
}