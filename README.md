![badge][badge-android]
![badge][badge-js]
![badge][badge-jvm]
[![codecov](https://codecov.io/gh/JuulLabs/krayon/branch/main/graph/badge.svg?token=y8btx3pTlr)](https://codecov.io/gh/JuulLabs/krayon)

# Krayon

Multiplatform drawing library. Provides a multiplatform canvas and chart rendering framework.

## Modules

### Aggregate Modules

| Module                           | Description |
|----------------------------------|-------------|
| [`box`](box)                     | Module exporting all other published modules. Useful for getting started quickly if your builds include dead-code elimination. |
| [`sample`](sample) (unpublished) | Sample application showing use of the library with common drawing code and logic across Android and JS targets. |

### Low Level Modules

These modules provide an unopinionated multiplatform experience with simple, low-level primatives. Drawing with a Krayon
`Kanvas` should feel very similar to drawing directly to an Android `Canvas` or HTML/JS `CanvasRenderingContext2D`.

| Module             | Description |
|--------------------|-------------|
| [`color`](color)   | Multiplatform color representation and operations. |
| [`kanvas`](kanvas) | `Kanvas` interface with platform specific (Android, HTML) and multiplatform (SVG) implementations. |

### High Level Modules

These modules provide functionality heavily inspired by, and often directly translated from, D3. Many of the modules aim
to provide a 1-to-1 mapping with a D3 library, such as `axis` or `selection`. Other modules, like `element`, attempt to
fill in gaps required to support this, like `Element` being used instead of the DOM to enable non-web targets.

| Module                         | Description |
|--------------------------------|-------------|
| [`axis`](axis)                 | Component for rendering reference marks as part of a chart. |
| [`color`](color)               | Multiplatform color representation and operations. |
| [`element`](element)           | `Element` class used to provide an mutable object-tree of drawing operation. |
| [`element-view`](element-view) | Android specific `View` for rendering an `Element` off the main thread. |
| [`interpolate`](interpolate)   | Functionality for blending/picking values between start and stop values. |
| [`kanvas`](kanvas)             | Unopinionated canvas with platform specific (Android, HTML) and multiplatform (SVG) targets. |
| [`selection`](selection)       | Data-driven transformation of the `Element`-tree. |
| [`shape`](shape)               | Visualization support for generating complex shapes from data, such as line charts. |

# License

```
Copyright 2021 JUUL Labs, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

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
