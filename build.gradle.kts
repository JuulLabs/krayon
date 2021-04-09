import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import java.net.URI

buildscript {
    repositories {
        google()
        jcenter()
    }
}

plugins {
    kotlin("multiplatform") version "1.4.32" apply false
    id("com.android.application") version "4.1.3" apply false
    id("com.android.library") version "4.1.3" apply false
    id("kotlinx-atomicfu") version "0.14.4" apply false
    id("org.jmailen.kotlinter") version "3.2.0" apply false
    id("binary-compatibility-validator") version "0.2.3"
    id("org.jetbrains.dokka") version "1.4.30"
    id("com.vanniktech.maven.publish") version "0.14.0" apply false
    id("net.mbonnin.one.eight") version "0.1"
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx/")
    }

    tasks.withType<Test>().configureEach {
        testLogging {
            events("started", "passed", "skipped", "failed", "standardOut", "standardError")
            exceptionFormat = FULL
            showExceptions = true
            showStackTraces = true
            showCauses = true
        }
    }
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(buildDir.resolve("gh-pages"))
}
