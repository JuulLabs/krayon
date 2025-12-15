enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "krayon"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Provides repositories for auto-downloading JVM toolchains.
    // https://github.com/gradle/foojay-toolchains
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

includeBuild("build-logic")

include(
    "axis",
    "box",
    "color",
    "compose",
    "core",
    "element",
    "element-view",
    "hierarchy",
    "interpolate",
    "kanvas",
    "sample",
    "scale",
    "selection",
    "shape",
    "time",
)
