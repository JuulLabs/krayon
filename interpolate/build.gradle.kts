plugins {
    id("library-conventions")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.kanvas)
            api(libs.datetime)
            implementation(projects.time)
        }
    }
}
