plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jmailen.kotlinter")
}

// TODO: When we add other platforms, this should become a multiplatform module.

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
