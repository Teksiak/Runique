package com.teksiak.auth.presentation.login

sealed interface LoginAction {
    data object OnTogglePasswordVisibility : LoginAction
    data object OnLoginClick : LoginAction
    data object OnSignUpClick : LoginAction
}