plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    dependencies {
        implementation("io.github.oshai:kotlin-logging:7.0.7")
    }
    sourceSets {
        androidMain.dependencies {
            implementation("io.github.oshai:kotlin-logging-android:7.0.7")
        }
    }
    explicitApi()
}

android {
    namespace = "com.imcys.bilibilias.core.logging"
}