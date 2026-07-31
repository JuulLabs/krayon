buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("repository-conventions")
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.api)
}

// Aggregates each module's documentation into a single site (under `build/dokka/html`),
// published to https://juullabs.github.io/krayon/api/ by the `gh-pages` workflow.
dependencies {
    dokka(project(":axis"))
    dokka(project(":box"))
    dokka(project(":color"))
    dokka(project(":compose"))
    dokka(project(":core"))
    dokka(project(":element"))
    dokka(project(":element-view"))
    dokka(project(":hierarchy"))
    dokka(project(":interpolate"))
    dokka(project(":kanvas"))
    dokka(project(":scale"))
    dokka(project(":selection"))
    dokka(project(":shape"))
    dokka(project(":time"))
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }

    ignoredProjects.addAll(listOf("android", "documentation", "sample", "shared"))
}
