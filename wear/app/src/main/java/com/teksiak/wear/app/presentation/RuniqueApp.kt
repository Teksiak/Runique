package com.teksiak.wear.app.presentation

import android.app.Application
import com.teksiak.wear.run.presentation.di.runPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RuniqueApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                runPresentationModule
            )
        }
    }
}