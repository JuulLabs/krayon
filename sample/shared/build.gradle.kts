import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("kotlin-conventions")
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    android {
        namespace = "com.juul.krayon.sample.shared"
        compileSdk = libs.versions.android.compile.get().toInt()
        minSdk = libs.versions.android.min.get().toInt()
    }

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
