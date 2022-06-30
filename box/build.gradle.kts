plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    explicitApi()

    android { publishAllLibraryVariants() }
    val iosArm64 = iosArm64()
    val iosSimulatorArm64 = iosSimulatorArm64()
    val iosX64 = iosX64()
    jvm()
    js().browser()
    val macosArm64 = macosArm64()
    val macosX64 = macosX64()

    val appleTargets = listOf(iosArm64, iosSimulatorArm64, iosX64, macosArm64, macosX64)
    configure(appleTargets) {
        binaries {
            framework {
                baseName = "Krayon"
                export(project(":axis"))
                export(project(":color"))
                export(project(":element"))
                export(project(":hierarchy"))
                export(project(":interpolate"))
                export(project(":kanvas"))
                export(project(":scale"))
                export(project(":selection"))
                export(project(":shape"))
                export(project(":time"))
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":axis"))
                api(project(":color"))
                api(project(":element"))
                api(project(":hierarchy"))
                api(project(":interpolate"))
                api(project(":kanvas"))
                api(project(":scale"))
                api(project(":selection"))
                api(project(":shape"))
                api(project(":time"))
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
                api(project(":element-view"))
            }
        }

        val androidTest by getting {
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
                api(project(":element-view"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

android {
    compileSdkVersion(libs.versions.android.compile.get())

    defaultConfig {
        minSdkVersion(libs.versions.android.min.get())
    }

    lintOptions {
        isAbortOnError = true
        isWarningsAsErrors = true
    }

    sourceSets {
        getByName("main").manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}
