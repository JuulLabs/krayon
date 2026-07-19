plugins {
    id("library-conventions")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.element)
            api(projects.transition)
            api(libs.coroutines.core)
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
