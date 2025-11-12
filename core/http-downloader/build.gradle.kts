plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.ktorClient)
            implementation(projects.core.common)

            api(libs.androidx.datastore)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.http.downloader"
}