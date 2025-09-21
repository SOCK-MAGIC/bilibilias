plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.ass"
}