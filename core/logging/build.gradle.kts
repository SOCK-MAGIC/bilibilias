plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    dependencies {
        api(libs.kotlin.logging)
    }
    sourceSets {
        jvmMain.dependencies {
            implementation(libs.log4j.slf4j2.impl)
            implementation(libs.log4j.core)
        }
    }
    explicitApi()
}

android {
    namespace = "com.imcys.bilibilias.core.logging"
}