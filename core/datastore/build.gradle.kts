plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    dependencies {
        implementation(projects.core.model)
        implementation(projects.core.common)

        implementation(libs.kotlinx.serialization.json)

        implementation(libs.store5)

        implementation(libs.androidx.datastore)
    }
}

android {
    namespace = "com.imcys.bilibilias.core.datastore"
}