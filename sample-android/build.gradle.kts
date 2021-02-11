plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jmailen.kotlinter")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "sample"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":canvas"))
}
