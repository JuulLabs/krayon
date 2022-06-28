plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    explicitApi()

    android { publishAllLibraryVariants() }
    jvm()
    js().browser()
    macosArm64()
    macosX64()
    iosArm64()

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
