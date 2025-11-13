plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    dependencies {
        implementation(libs.kotlin.logging)
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.kotlin.logging.android)
        }
    }
    explicitApi()
}

android {
    namespace = "com.imcys.bilibilias.core.logging"
}