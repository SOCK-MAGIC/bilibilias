plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    dependencies {
        api(libs.kotlin.logging)
    }
    sourceSets {
        jvmMain.dependencies {
            implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.2")
            implementation("org.apache.logging.log4j:log4j-core:2.25.2")
        }
    }
    explicitApi()
}

android {
    namespace = "com.imcys.bilibilias.core.logging"
}