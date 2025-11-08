plugins {
    alias(libs.plugins.bilibilias.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.datastore)

            api(libs.kotlinx.serialization.core)
            api(libs.ktor.client.core)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.serialization.kotlinx.json)
            api(libs.ktor.serialization.kotlinx.protobuf)

            api(libs.ktor.client.logging)

            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.ktor.client"
}