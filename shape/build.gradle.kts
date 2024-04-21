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

    iosArm64()
    js().browser()
    jvm()
    macosArm64()
    macosX64()

    sourceSets {
        commonMain.dependencies {
            api(projects.kanvas)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
