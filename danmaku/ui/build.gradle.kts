plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.danmaku.api)

            implementation(projects.core.common)

            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
    }
}
android {
    namespace = "com.imcys.bilibilias.danmaku.ui"
}