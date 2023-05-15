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

    android().publishAllLibraryVariants()
    jvm()
    js().browser()
    iosArm64()
    macosArm64()
    macosX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.color)
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
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.coroutines.android)
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

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val nativeDarwinMain by creating {
            dependsOn(commonMain)
        }

        val iosArm64Main by getting {
            dependsOn(nativeDarwinMain)
        }

        val macosArm64Main by getting {
            dependsOn(nativeDarwinMain)
        }

        val macosX64Main by getting {
            dependsOn(nativeDarwinMain)
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
    defaultConfig.minSdk = libs.versions.android.min.get().toInt()

    namespace = "com.juul.krayon.kanvas"

    lint {
        abortOnError = true
        warningsAsErrors = true
    }
}
