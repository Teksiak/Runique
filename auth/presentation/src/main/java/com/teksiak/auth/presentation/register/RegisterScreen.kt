@file:OptIn(ExperimentalFoundationApi::class)

package com.teksiak.auth.presentation.register

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teksiak.auth.domain.PasswordValidationState
import com.teksiak.auth.domain.UserDataValidator
import com.teksiak.auth.presentation.R
import com.teksiak.core.presentation.designsystem.CheckIcon
import com.teksiak.core.presentation.designsystem.CrossIcon
import com.teksiak.core.presentation.designsystem.EmailIcon
import com.teksiak.core.presentation.designsystem.Poppins
import com.teksiak.core.presentation.designsystem.RuniqueDarkRed
import com.teksiak.core.presentation.designsystem.RuniqueGray
import com.teksiak.core.presentation.designsystem.RuniqueGreen
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.GradientBackground
import com.teksiak.core.presentation.designsystem.components.RuniqueActionButton
import com.teksiak.core.presentation.designsystem.components.RuniquePasswordTextField
import com.teksiak.core.presentation.designsystem.components.RuniqueTextField
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreenRoot(
    onSignInClick: () -> Unit,
    onSuccessfulRegister: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    RegisterScreen(
        onAction = { action ->
            when (action) {
                RegisterAction.OnLoginClick -> onSignInClick()
                RegisterAction.OnRegisterClick -> onSuccessfulRegister()
                RegisterAction.OnTogglePasswordVisibilityClick -> TODO()
            }
        },
        state = viewModel.state
    )
}

@Composable
private fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.create_account),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontFamily = Poppins,
                        color = RuniqueGray
                    )
                ) {
                    append(stringResource(id = R.string.already_have_an_account) + " ")
                    pushStringAnnotation(
                        tag = "clickable_text",
                        annotation = stringResource(id = R.string.sign_in)
                    )
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = Poppins
                        )
                    ) {
                        append(stringResource(id = R.string.sign_in))
                    }
                }
            }
            ClickableText(
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "clickable_text",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        onAction(RegisterAction.OnLoginClick)
                    }
                }
            )
            Spacer(modifier = Modifier.height(48.dp))

            RuniqueTextField(
                state = state.email,
                modifier = Modifier.fillMaxWidth(),
                hint = stringResource(id = R.string.example_email),
                title = stringResource(id = R.string.email),
                startIcon = EmailIcon,
                endIcon = if (state.isEmailValid) {
                    CheckIcon
                } else null,
                additionalInfo = stringResource(id = R.string.must_be_valid_email),
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(16.dp))

            RuniquePasswordTextField(
                state = state.email,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = {
                    onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                },
                modifier = Modifier.fillMaxWidth(),
                hint = stringResource(id = R.string.password),
                title = stringResource(id = R.string.password),
            )
            Spacer(modifier = Modifier.height(16.dp))

            PasswordRequirement(
                text = stringResource(
                    id = R.string.at_least_x_characters,
                    UserDataValidator.MIN_PASSWORD_LENGTH
                ),
                isValid = state.passwordValidationState.hasMinLength,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            PasswordRequirement(
                text = stringResource(
                    id = R.string.at_least_one_number
                ),
                isValid = state.passwordValidationState.hasNumber,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            PasswordRequirement(
                text = stringResource(
                    id = R.string.contains_lowercase_characters,
                ),
                isValid = state.passwordValidationState.hasLowerCaseCharacter,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            PasswordRequirement(
                text = stringResource(
                    id = R.string.contains_uppercase_characters,
                ),
                isValid = state.passwordValidationState.hasUpperCaseCharacter,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            RuniqueActionButton(
                text = stringResource(id = R.string.register),
                isLoading = state.isRegistering,
                enabled = state.passwordValidationState.isValid,
                onClick = {
                    onAction(RegisterAction.OnRegisterClick)
                },
            )
        }
    }
}

@Composable
fun PasswordRequirement(
    text: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor by animateColorAsState(
        targetValue = if (isValid) RuniqueGreen else RuniqueDarkRed,
        label = "Icon color"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isValid) CheckIcon else CrossIcon,
                contentDescription = null,
                tint = iconColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    RuniqueTheme {
        RegisterScreen(
            state = RegisterState(
                passwordValidationState = PasswordValidationState(
                    hasMinLength = true,
                    hasNumber = true,
                    hasLowerCaseCharacter = true,
                    hasUpperCaseCharacter = true
                ),
            ),
            onAction = {},
        )
    }
}