enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven { setUrl("https://androidx.dev/storage/compose-compiler/repository/") }
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "binary-compatibility-validator" ->
                    useModule("org.jetbrains.kotlinx:binary-compatibility-validator:${requested.version}")

                "kotlinx-atomicfu" ->
                    useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")

                else -> when (requested.id.namespace) {
                    "com.android" ->
                        useModule("com.android.tools.build:gradle:${requested.version}")
                }
            }
        }
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
)
