plugins {
    kotlin("multiplatform")
    id("org.jmailen.kotlinter")
    jacoco
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))
apply(from = rootProject.file("gradle/publish.gradle.kts"))

jacoco {
    toolVersion = "0.8.7"
}

kotlin {
    explicitApi()

    jvm()
    js().browser()

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }

        val commonTest by getting {
            dependencies {
                implementation(tuulbox.test())
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
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
    }
}
