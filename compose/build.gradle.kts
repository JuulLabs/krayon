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
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    androidTarget().publishAllLibraryVariants()
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            api(projects.kanvas)
            api(projects.element)
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material)
            implementation(libs.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        getByName("desktopMain").dependencies {
            api(compose.preview)
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
    }
}
