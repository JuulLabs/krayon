enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
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
