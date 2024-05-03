package com.teksiak.runique

import android.app.Application
import com.teksiak.auth.data.di.authDataModule
import com.teksiak.auth.presentation.di.authViewModelModule
import com.teksiak.core.data.di.coreDataModule
import com.teksiak.runique.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
             Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                appModule,
                coreDataModule,
                authDataModule,
                authViewModelModule,
            )
        }
    }
}