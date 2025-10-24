plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.kotlinAtomicfu)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.ass"
    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++11"
            }
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/commonMain/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}