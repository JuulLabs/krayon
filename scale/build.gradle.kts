plugins {
    id("library-conventions")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.interpolate)
            api(projects.kanvas)
            api(projects.time)
            api(libs.datetime)
        }
    }
}
