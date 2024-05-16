package com.teksiak.analytics.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.android.play.core.splitcompat.SplitCompat
import org.koin.core.context.loadKoinModules
import com.teksiak.analytics.data.di.analyticsModule
import com.teksiak.analytics.presentation.di.analyticsPresentationModule
import com.teksiak.analytics.presentation.dashboard.AnalyticsDashboardScreenRoot
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.GradientBackground
import com.teksiak.runique.Routes

class AnalyticsActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(
            listOf(
                analyticsModule,
                analyticsPresentationModule
            )
        )
        SplitCompat.installActivity(this)

        setContent {
            RuniqueTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "analytics_dashboard"
                ) {
                    composable("analytics_dashboard") {
                        AnalyticsDashboardScreenRoot(
                            onBackClick = { finish() }
                        )
                    }
                    composable(
                        route = "compare_run/{runId}",
                        arguments = listOf(
                            navArgument("runId") {
                                type = NavType.StringType
                            }
                        ),
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "runique://analytics_compare_run/{runId}"
                            }
                        )
                    ) { backStackEntry ->
                        val runId = backStackEntry.arguments?.getString("runId")

                        RuniqueTheme {
                            GradientBackground {
                                Column(
                                    modifier = Modifier.padding(32.dp)
                                ) {
                                    Text(text = "Run ID: $runId")
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}