plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.navigation3.runtime)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.savedstate.compose)
            implementation(libs.kotlinx.serialization.core)

            implementation(libs.androidx.lifecycle.viewmodel.savedstate)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.navigation"
}