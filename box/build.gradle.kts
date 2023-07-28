plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    android().publishAllLibraryVariants()
    jvm()
    js().browser()
    macosArm64()
    macosX64()
    iosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
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
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                api(projects.elementView)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.androidx.test)
                implementation(libs.robolectric)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jsMain by getting {
            dependencies {
                api(projects.elementView)
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
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
