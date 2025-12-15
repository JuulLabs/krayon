plugins {
    id("library-conventions")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.scale)
            api(projects.selection)
        }
    }
}
