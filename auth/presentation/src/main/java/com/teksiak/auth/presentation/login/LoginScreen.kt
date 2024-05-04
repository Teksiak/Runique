@file:OptIn(ExperimentalFoundationApi::class)

package com.teksiak.auth.presentation.login

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
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
import com.teksiak.core.presentation.designsystem.RuniqueWhite
import com.teksiak.core.presentation.designsystem.components.GradientBackground
import com.teksiak.core.presentation.designsystem.components.RuniqueActionButton
import com.teksiak.core.presentation.designsystem.components.RuniquePasswordTextField
import com.teksiak.core.presentation.designsystem.components.RuniqueTextField
import com.teksiak.core.presentation.ui.ObserveAsEvents
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    onSignUpClick: () -> Unit,
    onSuccessfulLogin: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is LoginEvent.Error -> {
                keyboardController?.hide()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = event.error.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
            }

            LoginEvent.LoginSuccess -> {
                keyboardController?.hide()
                onSuccessfulLogin()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.youre_logged_in),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
    ) {
        LoginScreen(
            state = viewModel.state,
            onAction = { action ->
                when (action) {
                    LoginAction.OnSignUpClick -> onSignUpClick()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )

        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackbarHostState,
            snackbar = { snackbarData ->
                val isErrorSnackbar =
                    snackbarData.visuals.message != stringResource(id = R.string.registration_successful)
                            && snackbarData.visuals.message != stringResource(id = R.string.youre_logged_in)
                Snackbar(
                    snackbarData = snackbarData,
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = if (isErrorSnackbar) RuniqueDarkRed else MaterialTheme.colorScheme.primary,
                    contentColor = if (isErrorSnackbar) RuniqueWhite else MaterialTheme.colorScheme.onPrimary,
                )
            },
        )
    }
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(top = 32.dp)
        ) {
            Text(
                text = stringResource(id = R.string.hi_there),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(id = R.string.login_welcome),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(48.dp))

            RuniqueTextField(
                state = state.email,
                modifier = Modifier.fillMaxWidth(),
                hint = stringResource(id = R.string.example_email),
                title = stringResource(id = R.string.email),
                startIcon = EmailIcon,
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(16.dp))

            RuniquePasswordTextField(
                state = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = {
                    onAction(LoginAction.OnTogglePasswordVisibility)
                },
                modifier = Modifier.fillMaxWidth(),
                hint = stringResource(id = R.string.password),
                title = stringResource(id = R.string.password),
            )
            Spacer(modifier = Modifier.height(32.dp))

            RuniqueActionButton(
                text = stringResource(id = R.string.login),
                isLoading = state.isLoggingIn,
                enabled = state.canLogin,
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                },
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontFamily = Poppins,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                ) {
                    append(stringResource(id = R.string.dont_have_an_account) + " ")
                    pushStringAnnotation(
                        tag = "clickable_text",
                        annotation = stringResource(id = R.string.sign_up)
                    )
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = Poppins
                        )
                    ) {
                        append(stringResource(id = R.string.sign_up))
                    }
                }
            }
            ClickableText(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "clickable_text",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        onAction(LoginAction.OnSignUpClick)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    RuniqueTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {},
        )
    }
}