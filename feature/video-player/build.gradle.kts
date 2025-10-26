plugins {
    alias(libs.plugins.bilibilias.feature)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
        }
        commonMain.dependencies {
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.feature.videoplayer"
}