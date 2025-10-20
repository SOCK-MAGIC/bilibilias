plugins {
    alias(libs.plugins.bilibilias.kmp.library)
    alias(libs.plugins.kotlinAtomicfu)
    alias(libs.plugins.kotlinSerialization)
//    alias(libs.plugins.gobley.cargo)
//    alias(libs.plugins.gobley.uniffi)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)

            implementation("io.github.pdvrieze.xmlutil:serialization:0.91.2")
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.ass"
}

//uniffi {
//    bindgenFromPath(rootProject.layout.projectDirectory.dir("crates/gobley-uniffi-bindgen"))
//    generateFromLibrary()
//}