plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.designsystem)
            api(projects.core.model)

            implementation(libs.coil)
            implementation(libs.coil.compose)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.ui"
}