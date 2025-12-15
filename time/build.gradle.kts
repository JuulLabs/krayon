plugins {
    id("library-conventions")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.datetime)
        }
    }
}
