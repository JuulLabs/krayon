plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jmailen.kotlinter")
}

kotlin {
    android()
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":chart"))
                implementation(project(":scale"))
                implementation(project(":shape"))
                implementation(kotlin("stdlib"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(tuulbox.test())
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(androidx.appCompat())
                implementation(androidx.lifecycle("runtime-ktx"))
                implementation(kotlinx.coroutines("android"))
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlinx.coroutines("core-js"))
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
    compileSdkVersion(AndroidSdk.Compile)

    defaultConfig {
        minSdkVersion(AndroidSdk.Minimum)
        targetSdkVersion(AndroidSdk.Target)
        versionCode = 1
        versionName = "sample"
    }

    buildFeatures {
        resValues = true
        viewBinding = true
    }

    lintOptions {
        // Good habits for a real app, but trying to keep the sample project minimal.
        disable += "AllowBackup"
        disable += "MissingApplicationIcon"
        disable += "Overdraw"
        // False positives for some reason
        disable += "MissingClass"
        disable += "UnusedResources"
    }

    sourceSets {
        val main by getting {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}
