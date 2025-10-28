plugins {
    alias(libs.plugins.bilibilias.feature)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.danmaku)
            implementation(projects.core.videoPlayer)

            implementation(libs.androidx.compose.runtime.retain)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.feature.videoplayer"
}