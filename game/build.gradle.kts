import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    js {
        binaries.executable()
        browser {
            testTask { useKarma { useChromeHeadless() } }
            commonWebpackConfig {
                devServer =
                    (devServer
                        ?: org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
                            .DevServer())
                        .copy(
                            open = mapOf("app" to mapOf("name" to "chrome")),
                        )
            }
        }

        this.attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.js)

        compilerOptions { sourceMap = true }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.littlekt.core)
                implementation(libs.littlekt.scenegraph)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.html.js)
            }
        }
        val jsTest by getting
    }
}
