plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jmailen.kotlinter")
    jacoco
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    androidTarget().publishAllLibraryVariants()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    js().browser()
    jvm()
    macosArm64()
    macosX64()

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        commonMain.dependencies {
            api(projects.color)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.coroutines.android)
        }

        getByName("androidUnitTest").dependencies {
            implementation(libs.androidx.test)
            implementation(libs.robolectric)
        }
    }
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()
    defaultConfig.minSdk = libs.versions.android.min.get().toInt()

    namespace = "com.juul.krayon.kanvas"

    lint {
        abortOnError = true
        warningsAsErrors = true
    }
}
