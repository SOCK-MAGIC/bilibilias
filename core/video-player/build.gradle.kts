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
            implementation(libs.androidx.media3.ui)

            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.compose.ui.tooling)
        }
        commonMain.dependencies {
            implementation(projects.core.ui)

            api(mediampLibs.mediamp.api)

            implementation(libs.androidx.annotation)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.videoplayer"
}