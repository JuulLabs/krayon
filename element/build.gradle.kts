import org.jetbrains.compose.compose

plugins {
    id("library-conventions")
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.kanvas)
            implementation(compose.runtime)
        }
    }
}
