package com.teksiak.runique

import android.app.Application
import com.teksiak.auth.data.di.authDataModule
import com.teksiak.auth.presentation.di.authViewModelModule
import com.teksiak.core.data.di.coreDataModule
import com.teksiak.core.database.di.databaseModule
import com.teksiak.run.data.di.runDataModule
import com.teksiak.run.location.di.locationModule
import com.teksiak.run.network.di.networkModule
import com.teksiak.run.presentation.di.runPresentationModel
import com.teksiak.runique.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
             Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            workManagerFactory()
            modules(
                appModule,
                coreDataModule,
                authDataModule,
                authViewModelModule,
                runPresentationModel,
                locationModule,
                databaseModule,
                networkModule,
                runDataModule
            )
        }
    }
}