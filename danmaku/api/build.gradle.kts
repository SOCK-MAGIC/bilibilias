plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    dependencies {
        implementation(libs.kotlinx.coroutines.core)
    }
}

android {
    namespace = "com.imcys.bilibilias.danmaku.api"
}