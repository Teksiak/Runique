plugins {
    alias(libs.plugins.runique.android.library.compose)
    alias(libs.plugins.mapsplatformSescretsPlugin)
}

android {
    namespace = "com.teksiak.wear.run.presentation"

    defaultConfig {
        minSdk = 30
    }
}

dependencies {
    implementation(libs.androidx.wear)
    implementation(libs.androidx.wear.compose.material3)
    implementation(libs.androidx.wear.compose.material)
    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.androidx.wear.compose.ui.tooling)
    implementation(libs.play.services.wearable)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.koin.compose)
    implementation(libs.google.maps.android.compose)

    implementation(projects.core.presentation.designsystemWear)
    implementation(projects.core.presentation.ui)
    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)
    implementation(projects.core.notification)
    implementation(projects.wear.run.domain)
}