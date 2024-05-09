package com.teksiak.runique

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.teksiak.auth.presentation.R
import com.teksiak.auth.presentation.intro.IntroScreenRoot
import com.teksiak.auth.presentation.login.LoginScreenRoot
import com.teksiak.auth.presentation.register.RegisterScreenRoot
import com.teksiak.run.presentation.active_run.ActiveRunScreenRoot
import com.teksiak.run.presentation.active_run.service.ActiveRunService
import com.teksiak.run.presentation.run_overview.RunOverviewScreenRoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed interface Routes {
    object Auth {
        const val NAV_ROUTE = "auth"
        const val INTRO = "intro"
        const val REGISTER = "register"
        const val LOGIN = "login"
    }

    object Run {
        const val NAV_ROUTE = "run"
        const val RUN_OVERVIEW = "overview"
        const val ACTIVE_RUN = "active_run"

    }
}

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean,
) {
    NavHost(
        navController = navController,
        startDestination = if(isLoggedIn) Routes.Run.NAV_ROUTE else Routes.Auth.NAV_ROUTE
    ) {
        authGraph(navController)
        runGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
) {
    navigation(
        route = Routes.Auth.NAV_ROUTE,
        startDestination = Routes.Auth.INTRO
    ) {
        val sharedSnackbarHostState = SnackbarHostState()
        val snackbarScope = CoroutineScope(Dispatchers.Main)

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
            val context = LocalContext.current

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
                onSuccessfulRegistration = { snackbarHostState ->
                    navController.navigate(Routes.Auth.LOGIN) {
                        popUpTo(Routes.Auth.REGISTER) {
                            inclusive = true
                        }
                    }
                    snackbarScope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.registration_successful),
                            duration = SnackbarDuration.Short
                        )
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

private fun NavGraphBuilder.runGraph(navController: NavHostController) {
    navigation(
        route = Routes.Run.NAV_ROUTE,
        startDestination = Routes.Run.RUN_OVERVIEW
    ) {
        composable(Routes.Run.RUN_OVERVIEW) {
            val context = LocalContext.current

            RunOverviewScreenRoot(
                onStartRunClick = {
                    navController.navigate(Routes.Run.ACTIVE_RUN)
                },
                onStopService = {
                    context.startService(
                        ActiveRunService.createStopIntent(
                            context = context,
                        )
                    )
                }
            )
        }
        composable(
            route = Routes.Run.ACTIVE_RUN,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "runique://active_run"
                }
            )
        ) {
            val context = LocalContext.current
            ActiveRunScreenRoot(
                onBackClick = {
                    navController.navigate(Routes.Run.RUN_OVERVIEW) {
                        popUpTo(Routes.Run.ACTIVE_RUN) {
                            inclusive = true
                        }
                    }
                },
                onServiceToggle = { shouldServiceRun ->
                    if(shouldServiceRun) {
                        context.startService(
                            ActiveRunService.createStartIntent(
                                context = context,
                                activityClass = MainActivity::class.java
                            )
                        )
                    } else {
                        context.startService(
                            ActiveRunService.createStopIntent(
                                context = context,
                            )
                        )
                    }
                }
            )
        }
    }
}