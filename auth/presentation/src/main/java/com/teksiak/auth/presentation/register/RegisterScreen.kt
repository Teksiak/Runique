@file:OptIn(ExperimentalFoundationApi::class)

package com.teksiak.auth.presentation.register

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.GradientBackground
import com.teksiak.core.presentation.designsystem.components.RuniqueActionButton
import com.teksiak.core.presentation.designsystem.components.RuniquePasswordTextField
import com.teksiak.core.presentation.designsystem.components.RuniqueTextField
import com.teksiak.core.presentation.ui.ObserveAsEvents
import com.teksiak.core.presentation.ui.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreenRoot(
    onSignInClick: () -> Unit,
    onSuccessfulRegistration: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(viewModel.events) {event ->
        when(event) {
            is RegisterEvent.Error -> {
                keyboardController?.hide()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = event.error.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
            }
            RegisterEvent.RegistrationSuccess -> {
                keyboardController?.hide()
                onSuccessfulRegistration()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.registration_successful),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }


    RegisterScreen(
        snackbarHostState = snackbarHostState,
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun RegisterScreen(
    snackbarHostState: SnackbarHostState,
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .imePadding()
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
                state = state.password,
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
                enabled = state.canRegister,
                onClick = {
                    onAction(RegisterAction.OnRegisterClick)
                },
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    modifier = Modifier.padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = RuniqueDarkRed.copy(alpha = 0.6f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            },
        )
    }
}

@Composable
fun PasswordRequirement(
    text: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor by animateColorAsState(
        targetValue = if (isValid) MaterialTheme.colorScheme.primary else RuniqueDarkRed,
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
                modifier = Modifier.size(12.dp),
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
            snackbarHostState = SnackbarHostState(),
            state = RegisterState(
                passwordValidationState = PasswordValidationState(
                    hasMinLength = false,
                    hasNumber = true,
                    hasLowerCaseCharacter = false,
                    hasUpperCaseCharacter = true
                ),
            ),
            onAction = {},
        )
    }
}