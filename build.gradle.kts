buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    kotlin("multiplatform") version "1.5.20" apply false
    id("com.android.application") version "4.2.0" apply false
    id("com.android.library") version "4.2.0" apply false
    id("kotlinx-atomicfu") version "0.16.2" apply false
    id("org.jmailen.kotlinter") version "3.4.5" apply false
    id("org.jetbrains.dokka") version "1.5.0"
    id("com.vanniktech.maven.publish") version "0.17.0" apply false
    id("net.mbonnin.one.eight") version "0.2"

    // Breaks with:
    // A problem occurred configuring project ':registration'.
    // > org.gradle.api.internal.tasks.DefaultTaskContainer$DuplicateTaskException: Cannot add task 'apiBuild' as a task with that name already exists.
    //
    // Disabling until https://github.com/Kotlin/binary-compatibility-validator/issues/38 is fixed.
//    id("binary-compatibility-validator") version "0.6.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
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
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(buildDir.resolve("gh-pages"))
}
