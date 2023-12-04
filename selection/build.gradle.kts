plugins {
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

    jvm()
    js().browser()
    macosArm64()
    macosX64()
    iosArm64()

    sourceSets {
        commonMain.dependencies {
            api(projects.element)
        }

        commonTest.dependencies {
            implementation(libs.coroutines.test)
            implementation(kotlin("test"))
        }
    }
}
