plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    androidTarget().publishAllLibraryVariants()
    jvm()
    js().browser()
    macosArm64()
    macosX64()
    iosArm64()

    sourceSets {
        commonMain.dependencies {
            api(projects.axis)
            api(projects.color)
            api(projects.element)
            api(projects.hierarchy)
            api(projects.interpolate)
            api(projects.kanvas)
            api(projects.scale)
            api(projects.selection)
            api(projects.shape)
            api(projects.time)
        }

        androidMain.dependencies {
            api(projects.elementView)
        }

        jsMain.dependencies {
            api(projects.elementView)
        }
    }
}

// Uncomment for local builds for Apple targets
// kotlinArtifacts {
//     Native.XCFramework("Krayon") {
//         targets(macosArm64, macosX64, iosArm64)
//         modes(DEBUG, RELEASE)
//         addModule(projects.axis)
//         addModule(projects.color)
//         addModule(projects.element)
//         addModule(projects.hierarchy)
//         addModule(projects.interpolate)
//         addModule(projects.kanvas)
//         addModule(projects.scale)
//         addModule(projects.selection)
//         addModule(projects.shape)
//         addModule(projects.time)
//     }
// }

android {
    compileSdk = libs.versions.android.compile.get().toInt()
    defaultConfig.minSdk = libs.versions.android.min.get().toInt()

    namespace = "com.juul.krayon.box"

    lint {
        abortOnError = true
        warningsAsErrors = true
    }
}
