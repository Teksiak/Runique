plugins {
    alias(libs.plugins.runique.android.library.compose)
}

android {
    namespace = "com.teksiak.core.presentation.designsystem"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.coil.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.material3)
}