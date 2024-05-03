@file:OptIn(ExperimentalFoundationApi::class)

package com.teksiak.auth.presentation.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import com.teksiak.auth.domain.PasswordValidationState

data class LoginState(
    val email: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val isLoggingIn: Boolean = false,
    val canLogin: Boolean = false,
)
