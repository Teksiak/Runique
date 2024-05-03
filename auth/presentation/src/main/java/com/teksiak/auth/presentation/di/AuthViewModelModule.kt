package com.teksiak.auth.presentation.di

import com.teksiak.auth.presentation.login.LoginViewModel
import com.teksiak.auth.presentation.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authViewModelModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)
}