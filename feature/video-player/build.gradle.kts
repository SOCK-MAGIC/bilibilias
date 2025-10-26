plugins {
    alias(libs.plugins.bilibilias.feature)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.videoPlayer)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.feature.videoplayer"
}