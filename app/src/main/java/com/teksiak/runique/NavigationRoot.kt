package com.teksiak.runique

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.teksiak.auth.presentation.intro.IntroScreenRoot
import com.teksiak.auth.presentation.login.LoginScreenRoot
import com.teksiak.auth.presentation.register.RegisterScreenRoot

sealed interface Routes {
    object Auth {
        const val NAV_ROUTE = "auth"
        const val INTRO = "intro"
        const val REGISTER = "register"
        const val LOGIN = "login"
    }

    object Run {
        const val NAV_ROUTE = "run"
        const val HOME = "home"
    }
}

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Auth.NAV_ROUTE
    ) {
        authGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        route = Routes.Auth.NAV_ROUTE,
        startDestination = Routes.Auth.INTRO
    ) {
        val sharedSnackbarHostState = SnackbarHostState()

        composable(Routes.Auth.INTRO) {
            IntroScreenRoot(
                onSignUpClick = {
                    navController.navigate(Routes.Auth.REGISTER)
                },
                onSignInClick = {
                    navController.navigate(Routes.Auth.LOGIN)
                }
            )
        }
        composable(Routes.Auth.REGISTER) {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate(Routes.Auth.LOGIN) {
                        popUpTo(Routes.Auth.REGISTER) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate(Routes.Auth.LOGIN) {
                        popUpTo(Routes.Auth.REGISTER) {
                            inclusive = true
                        }
                    }
                },
                snackbarHostState = sharedSnackbarHostState
            )
        }
        composable(Routes.Auth.LOGIN) {
            LoginScreenRoot(
                onSignUpClick = {
                    navController.navigate(Routes.Auth.REGISTER) {
                        popUpTo(Routes.Auth.LOGIN) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulLogin = {
                    navController.navigate(Routes.Run.NAV_ROUTE) {
                        popUpTo(Routes.Auth.NAV_ROUTE) {
                            inclusive = true
                        }
                    }
                },
                snackbarHostState = sharedSnackbarHostState
            )
        }
    }
}