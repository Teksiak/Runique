package com.teksiak.runique.di

import android.content.SharedPreferences
import android.service.autofill.UserData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.teksiak.auth.data.EmailPatternValidator
import com.teksiak.auth.domain.PatternValidator
import com.teksiak.auth.domain.UserDataValidator
import com.teksiak.runique.MainViewModel
import com.teksiak.runique.RuniqueApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> {
        EncryptedSharedPreferences(
            androidApplication(),
            "auth_pref",
            MasterKey(androidApplication()),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    single<CoroutineScope> {
        (androidApplication() as RuniqueApp).applicationScope
    }

    viewModelOf(::MainViewModel)
}