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

kotlin {
    explicitApi()

    androidTarget().publishAllLibraryVariants()
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

tasks.withType<com.vanniktech.maven.publish.tasks.JavadocJar> {
    notCompatibleWithConfigurationCache("Attempts to directly use output of DokkaHtml tasks")
}
