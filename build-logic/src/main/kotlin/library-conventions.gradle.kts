group = "com.juul.krayon"

plugins {
    id("kotlin-conventions")
    alias(libs.plugins.android.library)
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

    androidTarget().publishLibraryVariants("debug", "release")
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    js().browser()
    jvm()
    macosArm64()
    macosX64()
    wasmJs().browser()
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()
    defaultConfig.minSdk = libs.versions.android.min.get().toInt()

    namespace = "com.juul.krayon.${project.name.replace("-", ".")}"

    lint {
        abortOnError = true
        warningsAsErrors = true
        disable += "GradleDependency"
    }
}
