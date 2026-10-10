plugins {
    id("library-conventions")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
        }
    }
}
