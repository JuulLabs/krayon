plugins {
    id("library-conventions")
}
kotlin {
    sourceSets {
        all {
            languageSettings.optIn("com.juul.krayon.core.InternalKrayonApi")
        }

        commonMain.dependencies {
            api(projects.color)
            api(projects.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.coroutines.android)
        }

        getByName("androidHostTest").dependencies {
            implementation(libs.androidx.test)
            implementation(libs.robolectric)
        }
    }
}
