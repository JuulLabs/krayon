![badge][badge-android]
![badge][badge-ios]
![badge][badge-js]
![badge][badge-jvm]

# Krayon

Multiplatform drawing library. Provides a multiplatform canvas and chart rendering framework,
heavily inspired by [D3](https://d3js.org/).

# Documentation

Guides, live examples, and the API reference are published at
**[juullabs.github.io/krayon](https://juullabs.github.io/krayon/)**:

- [What is Krayon?](https://juullabs.github.io/krayon/) — introduction and comparison to D3
- [Getting started](https://juullabs.github.io/krayon/#getting-started) — installation and your first chart
- [Gallery](https://juullabs.github.io/krayon/#gallery) — live, interactive examples with source code
- [API reference](https://juullabs.github.io/krayon/api/) — Dokka documentation for every module

# Modules

Krayon is published as small, focused modules under the `com.juul.krayon` group:

| Module | Description |
|---|---|
| `box` | Aggregate module exporting all other published modules (except `compose`) |
| `core` | Shared internals |
| `color` | Multiplatform color representation and operations |
| `kanvas` | `Kanvas` drawing interface with platform-specific (Android, HTML, Core Graphics) and multiplatform (SVG) implementations |
| `element` | Retained tree of drawing elements — Krayon's stand-in for the DOM |
| `selection` | Data-driven transformation of the element tree (à la [d3-selection]) |
| `scale` | Mapping data domains to visual ranges (à la [d3-scale]) |
| `shape` | Line, area, pie, and arc generators (à la [d3-shape]) |
| `axis` | Reference marks for scales (à la [d3-axis]) |
| `hierarchy` | Hierarchical data and treemap layout (à la [d3-hierarchy]) |
| `interpolate` | Blending/picking values between start and stop values (à la [d3-interpolate]) |
| `time` | Calendar intervals for time-based ticks (à la [d3-time]) |
| `compose` | Compose Multiplatform `Kanvas` and `ElementView` composables |
| `element-view` | Android `View` for rendering an element tree off the main thread |

[d3-selection]: https://d3js.org/d3-selection
[d3-scale]: https://d3js.org/d3-scale
[d3-shape]: https://d3js.org/d3-shape
[d3-axis]: https://d3js.org/d3-axis
[d3-hierarchy]: https://d3js.org/d3-hierarchy
[d3-interpolate]: https://d3js.org/d3-interpolate
[d3-time]: https://d3js.org/d3-time

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
