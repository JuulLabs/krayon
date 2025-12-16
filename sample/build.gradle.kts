import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("kotlin-conventions")
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    androidTarget()
    js {
        browser()
        binaries.executable()
    }
    jvm("desktop")
    wasmJs {
        outputModuleName = "application"
        browser { commonWebpackConfig { outputFileName = "application.js" } }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.coroutines.core)
            implementation(libs.datetime)
            implementation(projects.box)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(projects.compose)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.coroutines.android)
            implementation(libs.material)
            implementation(compose.foundation)
            implementation(compose.preview)
            implementation(compose.runtime)
            implementation(compose.ui)
        }

        getByName("desktopMain").dependencies {
            implementation(projects.compose)
            implementation(compose.desktop.currentOs)
            implementation(compose.preview)
        }

        jsMain.dependencies {
            implementation(libs.coroutines.js)
            implementation(compose.html.core) // required because of the compose plugin, but unused.
        }

        wasmJsMain.dependencies {
            implementation(projects.compose)
        }
    }
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
        versionCode = 1
        versionName = "sample"
    }

    namespace = "com.juul.krayon.sample"

    buildFeatures {
        resValues = true
        viewBinding = true
    }

    lint {
        // Good habits for a real app, but trying to keep the sample project minimal.
        disable += "AllowBackup"
        disable += "MissingApplicationIcon"
        disable += "Overdraw"
        // False positives for some reason
        disable += "MissingClass"
        disable += "UnusedResources"
        disable += "GradleDependency"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}
