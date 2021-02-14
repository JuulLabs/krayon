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
                api(project(":canvas"))
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

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "sample"
    }
}

dependencies {
    implementation(project(":canvas"))
}
