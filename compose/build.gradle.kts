import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jmailen.kotlinter")
    jacoco
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

kotlin {
    explicitApi()

    android() { publishAllLibraryVariants() }
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":kanvas"))
                api(project(":element"))
                api(compose.runtime.replace(libs.versions.compose.asProvider().get(), libs.versions.compose.fallback.get()))
                api(compose.foundation.replace(libs.versions.compose.asProvider().get(), libs.versions.compose.fallback.get()))
                api(compose.material.replace(libs.versions.compose.asProvider().get(), libs.versions.compose.fallback.get()))
                implementation(libs.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview.replace(libs.versions.compose.asProvider().get(), libs.versions.compose.fallback.get()))
            }
        }
    }
}

android {
    compileSdkVersion(libs.versions.android.compile.get())

    defaultConfig {
        minSdkVersion(libs.versions.android.min.get())
    }

    lintOptions {
        isAbortOnError = true
        isWarningsAsErrors = true
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}
