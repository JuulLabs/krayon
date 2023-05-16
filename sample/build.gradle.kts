import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.application")
    id("org.jmailen.kotlinter")
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    android()
    jvm("desktop")
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.box)
                implementation(kotlin("stdlib"))
                implementation(libs.coroutines.core)
                implementation(libs.datetime)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(projects.compose)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.coroutines.android)
                implementation(libs.material)
                implementation(compose.foundation)
                implementation(compose.preview)
                implementation(compose.runtime)
                implementation(compose.ui)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(projects.compose)
                implementation(compose.desktop.currentOs)
                implementation(compose.preview)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.coroutines.js)
                implementation(compose.web.core) // required because of the compose plugin, but unused.
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
    // Workaround (for `jvmToolchain` not being honored) needed until AGP 8.1.0-alpha09.
    // https://kotlinlang.org/docs/gradle-configure-project.html#gradle-java-toolchains-support
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
        versionCode = 1
        versionName = "sample"
    }

    buildFeatures {
        resValues = true
        viewBinding = true
    }

    lint {
        // Good habits for a real app, but trying to keep the sample project minimal.
        disable += "AllowBackup"
        disable += "MissingApplicationIcon"
        disable += "Overdraw"
        // False positives for some reason
        disable += "MissingClass"
        disable += "UnusedResources"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}
