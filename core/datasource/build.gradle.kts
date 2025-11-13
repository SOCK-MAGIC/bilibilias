plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.datastore)
            implementation(projects.core.ktorClient)
            implementation(projects.core.model)
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.datasource"
}