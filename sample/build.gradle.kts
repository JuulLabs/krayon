plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jmailen.kotlinter")
}

// TODO: When we add other platforms, this should become a multiplatform module.

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
}

dependencies {
    implementation(project(":chart"))
}
