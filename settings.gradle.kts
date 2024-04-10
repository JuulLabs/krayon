enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(
    "axis",
    "box",
    "color",
    "compose",
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
    "website",
)
