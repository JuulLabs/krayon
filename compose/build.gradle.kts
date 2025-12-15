plugins {
    id("library-conventions")
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("com.juul.krayon.core.InternalKrayonApi")
        }

        commonMain.dependencies {
            api(projects.core)
            api(projects.kanvas)
            api(projects.element)
            api(compose.components.resources)
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material)
            implementation(libs.datetime)
        }

        val skiaMain by creating {
            dependsOn(commonMain.get())
        }

        val jvmMain by getting {
            dependsOn(skiaMain)
        }

        val appleMain by getting {
            dependsOn(skiaMain)
        }

        val jsMain by getting {
            dependsOn(skiaMain)
        }

        val wasmJsMain by getting {
            dependsOn(skiaMain)
        }
    }
}

compose.resources { generateResClass = never }
