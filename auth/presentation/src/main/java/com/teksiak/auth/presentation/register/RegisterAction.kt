package com.teksiak.auth.presentation.register

sealed interface RegisterAction {
    data object OnTogglePasswordVisibility : RegisterAction
    data object OnSignInClick : RegisterAction
    data object OnRegisterClick : RegisterAction
}