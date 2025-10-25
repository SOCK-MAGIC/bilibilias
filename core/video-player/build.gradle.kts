plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(mediampLibs.mediamp.exoplayer)
            implementation(libs.androidx.media3.datasource.okhttp)
            implementation(libs.androidx.media3.exoplayer)
        }
        commonMain.dependencies {
            implementation(mediampLibs.mediamp.api)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.videoplayer"
}