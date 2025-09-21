plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.kotlinAtomicfu)
    alias(libs.plugins.gobley.cargo)
    alias(libs.plugins.gobley.uniffi)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.ass"
}