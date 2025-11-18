plugins {
    id("library-conventions")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.axis)
            api(projects.color)
            api(projects.element)
            api(projects.elementView)
            api(projects.hierarchy)
            api(projects.interpolate)
            api(projects.kanvas)
            api(projects.scale)
            api(projects.selection)
            api(projects.shape)
            api(projects.time)
        }
    }
}

// Uncomment for local builds for Apple targets
// kotlinArtifacts {
//     Native.XCFramework("Krayon") {
//         targets(macosArm64, macosX64, iosArm64)
//         modes(DEBUG, RELEASE)
//         addModule(projects.axis)
//         addModule(projects.color)
//         addModule(projects.element)
//         addModule(projects.hierarchy)
//         addModule(projects.interpolate)
//         addModule(projects.kanvas)
//         addModule(projects.scale)
//         addModule(projects.selection)
//         addModule(projects.shape)
//         addModule(projects.time)
//     }
// }
