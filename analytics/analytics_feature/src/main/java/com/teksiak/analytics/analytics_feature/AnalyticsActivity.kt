package com.teksiak.analytics.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitcompat.SplitCompat
import com.teksiak.analytics.data.di.analyticsModule
import com.teksiak.analytics.presentation.di.analyticsPresentationModule
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import org.koin.core.context.loadKoinModules

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
                NavigationRoot(
                    navController = navController,
                    finishActivity = this::finish
                )
            }
        }
    }
}