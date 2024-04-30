plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.teksiak.auth.presentation"
}

dependencies {

    implementation(projects.auth.domain)
    implementation(projects.core.domain)
}