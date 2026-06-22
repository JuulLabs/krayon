group = "com.juul.krayon"

plugins {
    id("kotlin-conventions")
    alias(libs.plugins.android.kotlin.multiplatform.library)
    jacoco
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
    explicitApi()

    android {
        namespace = "com.juul.krayon.${project.name.replace("-", ".")}"
        compileSdk = libs.versions.android.compile.get().toInt()
        minSdk = libs.versions.android.min.get().toInt()

        androidResources { enable = true }

        withHostTest {
            isIncludeAndroidResources = true
        }

        lint {
            abortOnError = true
            warningsAsErrors = true
            disable += "AndroidGradlePluginVersion"
            disable += "GradleDependency"
        }
    }

    iosArm64()
    iosSimulatorArm64()
    iosX64()
    js().browser()
    jvm()
    macosArm64()
    macosX64()
    wasmJs().browser()

    compilerOptions {
        allWarningsAsErrors = true
        extraWarnings = true
    }
}
