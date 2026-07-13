plugins {
    id("library-conventions")
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("com.juul.krayon.core.InternalKrayonApi")
        }

        commonMain.dependencies {
            api(projects.selection)
            api(projects.interpolate)
            api(projects.color)
            implementation(projects.core)
            implementation(projects.kanvas)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
