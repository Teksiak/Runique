plugins {
    alias(libs.plugins.runique.android.feature.ui)
    alias(libs.plugins.mapsplatformSescretsPlugin)
}

android {
    namespace = "com.teksiak.run.presentation"
}

dependencies {

    implementation(libs.coil.compose)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)

    implementation(projects.core.domain)
    implementation(projects.core.presentation.designsystem)
    implementation(projects.core.notification)
    implementation(projects.run.domain)
    implementation(projects.core.connectivity.domain)
}