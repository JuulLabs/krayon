plugins {
    id("repository-conventions")
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.kotlinter)
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    applyDefaultHierarchyTemplate()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }

        all {
            languageSettings.optIn("com.juul.krayon.core.ExperimentalKrayonApi")
            languageSettings.optIn("com.juul.krayon.core.InternalKrayonApi")
            if (name.startsWith("apple") || name.startsWith("ios") || name.startsWith("macos")) {
                languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    testLogging {
        events("started", "passed", "skipped", "failed", "standardOut", "standardError")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showStackTraces = true
        showCauses = true
    }
}
