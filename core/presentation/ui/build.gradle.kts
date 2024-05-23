plugins {
    alias(libs.plugins.runique.android.library.compose)
}

android {
    namespace = "com.teksiak.core.presentation.ui"
}

dependencies {

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material3)

    implementation(projects.core.domain)
}