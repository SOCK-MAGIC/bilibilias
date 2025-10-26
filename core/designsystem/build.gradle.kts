plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.uiPreview)

            api(compose.components.resources)
            api(compose.foundation)
            api(compose.ui)
            api(compose.runtime)
            api(compose.material3)
            api(compose.material3AdaptiveNavigationSuite)
            api(compose.materialIconsExtended)

            api(libs.androidx.compose.material3.adaptive)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.designsystem"
}