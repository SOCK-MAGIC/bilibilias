import com.android.build.gradle.LibraryExtension
import com.imcys.bilibilias.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "bilibilias.kmp.library")
            apply(plugin = "bilibilias.koin")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true

            }

            dependencies {
//                "commonMainImplementation"(project(":core:ui"))
                "commonMainImplementation"(project(":core:designsystem"))

                "commonMainImplementation"(
                    libs.findLibrary("androidx.lifecycle.runtime.compose").get()
                )
                "commonMainImplementation"(
                    libs.findLibrary("androidx.lifecycle.viewmodel.compose").get()
                )
//                "commonMainImplementation"(libs.findLibrary("koin.compose").get())
                "commonMainImplementation"(libs.findLibrary("koin.compose.viewmodel").get())

//                "androidMainImplementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
//                "androidMainImplementation"(libs.findLibrary("androidx.tracing.ktx").get())

//                "androidInstrumentedTestImplementation"(libs.findLibrary("androidx.compose.ui.test").get())
//                "androidInstrumentedTestImplementation"(libs.findLibrary("androidx.test.core").get())
//                "androidInstrumentedTestImplementation"(libs.findLibrary("androidx.test.ext").get())
//                "androidInstrumentedTestImplementation"(libs.findLibrary("androidx.test.junit").get())
//                "androidInstrumentedTestImplementation"(libs.findLibrary("androidx.test.runner").get())
            }
        }
    }
}