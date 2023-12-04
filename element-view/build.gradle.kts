plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jmailen.kotlinter")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    androidTarget().publishAllLibraryVariants()
    js().browser()

    sourceSets {
        commonMain.dependencies {
            api(projects.element)
            api(libs.coroutines.core)
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

    namespace = "com.juul.krayon.element.view"

    lint {
        abortOnError = true
        warningsAsErrors = true
    }
}
