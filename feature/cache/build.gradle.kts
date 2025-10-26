plugins {
    alias(libs.plugins.bilibilias.feature)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    dependencies {
        implementation(projects.core.data)
        implementation(projects.core.domain)
        implementation(projects.core.ffmpeg)
    }
}

android {
    namespace = "com.imcys.bilibilias.feature.cache"
}