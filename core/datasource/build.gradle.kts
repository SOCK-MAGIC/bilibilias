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
        }
    }
}

android {
    namespace = "com.imcys.bilibilias.core.datasource"
}

// in :core:datasource/build.gradle.kts
//dependencies {
//    api(projects.core.model)       // 需要访问 UserPreferences
//    implementation(projects.core.datastore) // 需要使用 DataStore 工廠
//}
// in :core:data/build.gradle.kts
//dependencies {
//    api(projects.core.datasource) // 需要访问 PreferencesDataSource 接口
//    api(projects.core.domain)     // 实现 domain 层的 Repository 接口
//}
// in :feature:settings/build.gradle.kts
//dependencies {
//    implementation(projects.core.domain) // 需要访问 UseCase
//    implementation(projects.core.model)  // ViewModel 可能需要处理 UserPreferences 对象
//}