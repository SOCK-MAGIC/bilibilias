plugins {
    alias(libs.plugins.bilibilias.feature)
    alias(libs.plugins.bilibilias.compose)
}

kotlin {
    dependencies {
        implementation(projects.core.data)

        implementation(libs.settings.ui)

        implementation(libs.reorderable)
    }
}

android {
    namespace = "com.imcys.bilibilias.feature.settings"
}