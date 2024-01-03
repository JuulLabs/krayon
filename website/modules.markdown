---
layout: page
title: Modules
permalink: /modules/
---

### Aggregate Modules

| Module                           | Description                                                                                                                                                                                   |
|----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`box`][box]                     | Module exporting all other published modules (except `compose`). Useful for getting started quickly if your builds include dead-code elimination. Also includes some cross-module extensions. |
| [`sample`][sample] (unpublished) | Sample application showing use of the library with common drawing code and logic across Android and JS targets.                                                                               |

### Low Level Modules

These modules provide an unopinionated multiplatform experience with simple, low-level primitives. Drawing with a Krayon
`Kanvas` should feel very similar to drawing directly to an Android `Canvas` or HTML/JS `CanvasRenderingContext2D`.

| Module             | Description                                                                                        |
|--------------------|----------------------------------------------------------------------------------------------------|
| [`color`][color]   | Multiplatform color representation and operations.                                                 |
| [`kanvas`][kanvas] | `Kanvas` interface with platform specific (Android, HTML) and multiplatform (SVG) implementations. |

### High Level Modules

These modules provide functionality heavily inspired by, and often directly translated from, D3. Many of the modules aim
to provide a 1-to-1 mapping with a D3 library, such as `axis` or `selection`. Other modules, like `element`, attempt to
fill in gaps required to support this, like `Element` being used instead of the DOM to enable non-web targets.

| Module                         | Description                                                                                  |
|--------------------------------|----------------------------------------------------------------------------------------------|
| [`axis`][axis]                 | Component for rendering reference marks as part of a chart.                                  |
| [`compose`][compose]           | Compose Multiplatform `Kanvas` and `ElementView` composables. Not included in `box`.         |
| [`element`][element]           | `Element` class used to provide an mutable object-tree of drawing operation.                 |
| [`element-view`][element-view] | Android specific `View` for rendering an `Element` off the main thread.                      |
| [`interpolate`][interpolate]   | Functionality for blending/picking values between start and stop values.                     |
| [`selection`][selection]       | Data-driven transformation of the `Element`-tree.                                            |
| [`shape`][shape]               | Visualization support for generating complex shapes from data, such as line charts.          |


[axis]: https://github.com/JuulLabs/krayon/tree/main/axis
[box]: https://github.com/JuulLabs/krayon/tree/main/box
[color]: https://github.com/JuulLabs/krayon/tree/main/color
[compose]: https://github.com/JuulLabs/krayon/tree/main/compose
[element-view]: https://github.com/JuulLabs/krayon/tree/main/element-view
[element]: https://github.com/JuulLabs/krayon/tree/main/element
[interpolate]: https://github.com/JuulLabs/krayon/tree/main/interpolate
[kanvas]: https://github.com/JuulLabs/krayon/tree/main/kanvas
[sample]: https://github.com/JuulLabs/krayon/tree/main/sample
[selection]: https://github.com/JuulLabs/krayon/tree/main/selection
[shape]: https://github.com/JuulLabs/krayon/tree/main/shape
