package com.teksiak.wear.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.teksiak.core.notification.ActiveRunService
import com.teksiak.core.presentation.designsystem_wear.RuniqueTheme
import com.teksiak.wear.run.presentation.TrackerScreenRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            RuniqueTheme {
                TrackerScreenRoot(
                    onServiceToggle = { shouldServiceRun ->
                        if(shouldServiceRun) {
                            startService(
                                ActiveRunService.createStartIntent(
                                    applicationContext, this::class.java
                                )
                            )
                        } else {
                            startService(
                                ActiveRunService.createStopIntent(
                                    applicationContext
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}
