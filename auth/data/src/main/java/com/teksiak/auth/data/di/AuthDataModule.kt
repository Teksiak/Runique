package com.teksiak.auth.data.di

import com.teksiak.auth.data.EmailPatternValidator
import com.teksiak.auth.domain.PatternValidator
import com.teksiak.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
}