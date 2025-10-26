pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
    versionCatalogs {
        create("mediampLibs") {
            from("org.openani.mediamp:catalog:0.0.29")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "bilibilias"

include(":app")
include(":core:ass")
include(":core:common")
include(":core:data")
include(":core:datasource")
include(":core:datastore")
include(":core:designsystem")
include(":core:domain")
include(":core:ffmpeg")
include(":core:ktor-client")
include(":core:http-downloader")
include(":core:model")
include(":core:navigation")
include(":core:ui-preview")
include(":core:video-player")
include(":logic")
include(":ui")
include(":feature:video-player")
