plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("org.jmailen.kotlinter")
    jacoco
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    androidTarget().publishAllLibraryVariants()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    js().browser()
    jvm("desktop")
    macosArm64()
    macosX64()

    sourceSets {

        applyDefaultHierarchyTemplate()

        all {
            languageSettings.optIn("com.juul.krayon.core.InternalKrayonApi")
        }

        commonMain.dependencies {
            api(projects.core)
            api(projects.kanvas)
            api(projects.element)
            api(compose.components.resources)
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material)
            implementation(libs.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        val skiaMain by creating {
            dependsOn(commonMain.get())
        }

        val desktopMain by getting {
            dependsOn(skiaMain)
        }

        val appleMain by getting {
            dependsOn(skiaMain)
        }

        val jsMain by getting {
            dependsOn(skiaMain)
        }
    }
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()
    defaultConfig.minSdk = libs.versions.android.min.get().toInt()

    namespace = "com.juul.krayon.compose"

    lint {
        abortOnError = true
        warningsAsErrors = true
        disable += "GradleDependency"
    }
}

compose.resources { generateResClass = never }
