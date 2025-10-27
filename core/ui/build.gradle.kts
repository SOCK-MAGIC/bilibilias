plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }

    dependencies {
        api(projects.core.designsystem)
        api(projects.core.model)


        implementation(libs.coil)
        implementation(libs.coil.compose)
    }
}

android {
    namespace = "com.imcys.bilibilias.core.ui"
}