plugins {
    id("repository-conventions")
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        applicationId = "com.juul.krayon.sample"
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
        versionCode = 1
        versionName = "sample"
    }

    namespace = "com.juul.krayon.sample"

    buildFeatures {
        resValues = true
        viewBinding = true
    }

    lint {
        // Good habits for a real app, but trying to keep the sample project minimal.
        disable += "AllowBackup"
        disable += "AndroidGradlePluginVersion"
        disable += "MissingApplicationIcon"
        disable += "Overdraw"
        // False positives for some reason
        disable += "MissingClass"
        disable += "UnusedResources"
        disable += "GradleDependency"
    }
}

dependencies {
    implementation(projects.sample)
    implementation(projects.compose)
    implementation(projects.elementView)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.coroutines.android)
    implementation(libs.material)
    implementation(compose.foundation)
    implementation(compose.preview)
    implementation(compose.runtime)
    implementation(compose.ui)
}
