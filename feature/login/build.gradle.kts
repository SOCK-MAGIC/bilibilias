plugins {
    alias(libs.plugins.bilibilias.feature)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    dependencies {
        implementation(projects.core.data)

        implementation(libs.flowredux)

        implementation(libs.coil.compose)

        implementation(libs.qr.kit)
    }
}

android {
    namespace = "com.imcys.bilibilias.feature.login"
}