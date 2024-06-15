plugins {
    alias(libs.plugins.bilibilias.android.feature)
    alias(libs.plugins.bilibilias.android.library.compose)
    alias(libs.plugins.bilibilias.android.library.jacoco)
    alias(libs.plugins.bilibilias.multiplatform.decompose)
}

android {
    namespace = "com.imcys.bilibilias.feature.settings"
}

dependencies {
    implementation(projects.feature.common)
    implementation(projects.core.common)
    implementation(projects.core.datastore)

    implementation(libs.compose.settings)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    implementation(libs.simple.storage)
}
