plugins {
    alias(libs.plugins.runique.android.feature.ui)
}

android {
    namespace = "com.teksiak.analytics.presentation"
}

dependencies {
    implementation(libs.coil.compose)

    implementation(projects.analytics.domain)
    implementation(projects.core.domain)
}