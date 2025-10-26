plugins {
    alias(libs.plugins.bilibilias.feature)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.core.httpDownloader)

            implementation(libs.coil.compose)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.feature.search"
}