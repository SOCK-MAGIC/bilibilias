import org.gradle.kotlin.dsl.test

plugins {
    alias(libs.plugins.bilibilias.android.feature)
    alias(libs.plugins.bilibilias.android.compose)
    alias(libs.plugins.bilibilias.android.jacoco)
    alias(libs.plugins.bilibilias.decompose)
}

android {
    namespace = "com.imcys.bilibilias.feature.tool"
}

dependencies {
    implementation(projects.feature.common)

    implementation(projects.core.domain)
    implementation(projects.core.download)

    implementation(libs.flexible.bottomsheet.material3)

    testImplementation(projects.core.testing)
    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
