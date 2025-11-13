plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    dependencies {
        api(libs.kotlin.logging)
    }
    sourceSets {
        androidMain.dependencies {
//            api(libs.kotlin.logging.android)
        }
    }
    explicitApi()
}

android {
    namespace = "com.imcys.bilibilias.core.logging"
}