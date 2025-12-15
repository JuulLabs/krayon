plugins {
    id("library-conventions")
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("com.juul.krayon.core.InternalKrayonApi")
        }

        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
        }
    }
}
