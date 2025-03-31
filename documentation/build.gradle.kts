import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    androidTarget()
    iosArm64 {
        binaries.framework {
            baseName = "DocumentationApp"
            binaryOption("bundleId", "com.juul.krayon.documentation.ios")
            binaryOption("bundleShortVersionString", "1.0.0")
            binaryOption("bundleVersion", "1")
        }
    }
    js {
        outputModuleName = "documentation"
        browser {
            commonWebpackConfig {
                outputFileName = "documentation.js"
            }
        }
        binaries.executable()
    }
    jvm()
    macosArm64 {
        binaries {
            executable {
                entryPoint = "com.juul.krayon.documentation.main"
            }
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            languageSettings.optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
        }

        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.coroutines.core)
            implementation(libs.highlights)
            implementation(libs.markdown)
            implementation(libs.navigation)
            implementation(libs.serialization)
            implementation(projects.box)
            implementation(projects.compose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.coroutines.android)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.preview)
            implementation(libs.coroutines.swing)
        }
    }
}

android {
    namespace = "com.juul.krayon.documentation"
    compileSdk = libs.versions.android.compile.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/commonMain/composeResources")
    sourceSets["main"].resources.srcDirs("src/commonMain/composeResources/files")

    defaultConfig {
        applicationId = "com.juul.krayon.documentation"
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures.compose = true
}

compose {
    desktop {
        application {
            mainClass = "com.juul.krayon.documentation.Main_jvmKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "documentation"
                packageVersion = "1.0.0"
            }

            buildTypes.release.proguard {
                version.set("7.4.0")
                configurationFiles.from(project.file("proguard-rules.pro"))
            }
        }
    }
}
