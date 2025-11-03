plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.bilibilias.compose)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.danmaku.api)

            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
    }
}
android {
    namespace = "com.imcys.bilibilias.danmaku.ui"
}