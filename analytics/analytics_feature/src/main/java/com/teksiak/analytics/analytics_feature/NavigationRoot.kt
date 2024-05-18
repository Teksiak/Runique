package com.teksiak.analytics.analytics_feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.teksiak.analytics.presentation.compare_run.CompareRunScreenRoot
import com.teksiak.analytics.presentation.dashboard.AnalyticsDashboardScreenRoot
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.GradientBackground

sealed interface Routes {
    object Analytics {
        const val DASHBOARD = "analytics_dashboard"
        const val COMPARE_RUN = "analytics_compare_run"
    }
}

@Composable
fun NavigationRoot(
    navController: NavHostController,
    finishActivity: () -> Unit
) {
   NavHost(
       navController = navController,
       startDestination = Routes.Analytics.DASHBOARD
   ) {
       composable(route = Routes.Analytics.DASHBOARD) {
           AnalyticsDashboardScreenRoot(
               onBackClick = finishActivity
           )
       }
       composable(
           route = "${Routes.Analytics.COMPARE_RUN}/{runId}",
           arguments = listOf(
               navArgument("runId") {
                   type = NavType.StringType
               }
           ),
           deepLinks = listOf(
               navDeepLink {
                   uriPattern = "runique://${Routes.Analytics.COMPARE_RUN}/{runId}"
               }
           )
       ) { backStackEntry ->
           val runId = backStackEntry.arguments?.getString("runId")

           CompareRunScreenRoot(
               onBackClick = finishActivity,
           )

       }
   }
}