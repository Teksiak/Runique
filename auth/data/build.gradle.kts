plugins {
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.jvm.ktor)
}

android {
    namespace = "com.teksiak.auth.data"
}

dependencies {

    implementation(projects.auth.domain)
    implementation(projects.core.domain)
}