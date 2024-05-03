package com.teksiak.auth.presentation.login

import com.teksiak.core.presentation.ui.UiText

sealed interface LoginEvent {
    data class Error(val error: UiText): LoginEvent
    data object LoginSuccess: LoginEvent
}