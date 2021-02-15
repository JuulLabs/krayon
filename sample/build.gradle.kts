plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jmailen.kotlinter")
}

// TODO: When we add other platforms, this should become a multiplatform module.

kotlin {
    android { publishAllLibraryVariants() }
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":chart"))
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
    }

    sourceSets {
        val main by getting {
            // FIXME: This feels like I'm working around some other misconfiguration.
            java.srcDirs("src/androidMain/java")
            res.srcDirs("src/androidMain/res")
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}
