plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            commonMain.dependencies {
                api(projects.core.data)
                api(projects.core.model)

                implementation(projects.core.ass)
                implementation(projects.core.httpDownloader)
            }
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.domain"
}