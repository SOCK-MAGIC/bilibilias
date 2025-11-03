import org.jetbrains.compose.compose

plugins {
    alias(libs.plugins.bilibilias.feature)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.danmaku.ui)
            implementation(projects.core.videoPlayer)

            implementation(libs.androidx.compose.runtime.retain)

            implementation(compose("org.jetbrains.compose.ui:ui-backhandler"))
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.feature.videoplayer"
}