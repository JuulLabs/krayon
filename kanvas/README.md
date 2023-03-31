![badge-android]
![badge-jvm]
![badge-js]
![badge-ios]
![badge-mac]

# Kanvas

Multiplatform canvas.

## Setup

### Gradle

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.juul.krayon/kanvas/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.juul.krayon/kanvas)

Canvas can be configured via Gradle Kotlin DSL as follows:

#### Multiplatform

```kotlin
plugins {
    id("com.android.application") // or id("com.android.library")
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    android()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.juul.krayon:kanvas:$version")
            }
        }
    }
}

android {
    // ...
}
```

#### Platform-specific

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.juul.krayon:kanvas-$platform:$version")
}
```

_Where `$platform` represents (should be replaced with) the desired platform dependency (e.g. `jvm`)._

### Apple Setup

Consuming Krayon for Apple targets (MacOS, iOS) requires additional setup, due to the inability to
load multiple Kotlin libraries to Swift/Objective-C. The intended technique is for Krayon to be
included directly by another Kotlin library which would contain shared application code, including
shared Krayon drawing. That library would then export Krayon as part of its native library for use
by iOS.

#### Using CocoaPods Plugin

Include the cocoapods plugin in your Kotlin module:

```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}
```

When defining your framework, make sure to export Krayon modules as needed:

```kotlin
cocoapods {
    // ...
    framework {
        // ...
        export("com.juul.krayon:$module:$version")
    }
}
```

#### Using Kotlin-Artifacts DSL

A simpler, but experimental, approach involves using the [Kotlin Artifacts DSL](https://kotlinlang.org/docs/multiplatform-native-artifacts.html).

```kotlin
kotlinArtifacts {
    Native.XCFramework("YourLibrary") {
        targets(macosArm64, macosX64, iosArm64)
        modes(DEBUG, RELEASE)
        addModule("com.juul.krayon:$module:$version")
    }
}
```

#### Using Krayon

The basic building block of Krayon for Apple is the `CGContextKanvas`, which allows drawing through
Krayon's multiplatform interface with Core Graphics' native drawing primitives. Krayon assumes the
`DeviceRGB` colorspace. For offscreen rendering, you can create your own:

```swift
// Define some constants. Here `scale` is used to handle retina 2x scaling
let width = 400
let height = 300
let scale = 2

// Create a context and pass it to Krayon
var cgContext: CGContext = CGContext(
    data: nil,
    width: width * scale,
    height: height * scale,
    bitsPerComponent: 8,
    bytesPerRow: 0,
    space: CGColorSpaceCreateDeviceRGB(),
    bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue
)!
cgContext.scaleBy(x: scale, y: scale)
// Note that Kanvas uses the unscaled size, which does not match the pixel count of the CGContext
let kanvas = CGContextKanvas(ptr: &cgContext, width: width, height: height)
```

Drawing to your Krayon canvas directly from Swift isn't recommend -- the API is quite ugly. From here,
you should call a Kotlin function that handles drawing for you. As an example, using the `element`
module, you might simply have `yourRootElement.draw(kanvas: kanvas)`.

[badge-android]: http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat
[badge-ios]: http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat
[badge-js]: http://img.shields.io/badge/platform-js-F8DB5D.svg?style=flat
[badge-jvm]: http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat
[badge-linux]: http://img.shields.io/badge/platform-linux-2D3F6C.svg?style=flat
[badge-windows]: http://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat
[badge-mac]: http://img.shields.io/badge/platform-macos-111111.svg?style=flat
[badge-watchos]: http://img.shields.io/badge/platform-watchos-C0C0C0.svg?style=flat
[badge-tvos]: http://img.shields.io/badge/platform-tvos-808080.svg?style=flat
[badge-wasm]: https://img.shields.io/badge/platform-wasm-624FE8.svg?style=flat
