plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    dependencies {

    }
    sourceSets {
        jvmMain.dependencies {
            implementation("org.slf4j:slf4j-api:2.0.17")
            implementation("uk.uuid.slf4j:slf4j-android:2.0.17-0")
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.logging"
}