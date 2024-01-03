---
layout: page
title: Tutorial
permalink: /tutorial/
mermaid: true
---

## Comparison to [D3]

> D3 (or D3.js) is a free, open-source JavaScript library for visualizing data.

D3 (which stands for "Data-Driven Documents") is commonly used to have a dataset drive manipulation
of the HTML [Document Object Model] (DOM).

```mermaid
graph LR;
    Data -- D3 --> DOM;
```

[D3]'s ["most central feature" is "data binding"](https://stackoverflow.com/a/50143500), whereas
data can drive the mutation of the HTML [DOM]. [D3] can also be used to draw to SVG or [Canvas], but
faux elements (rather than HTML [DOM] elements) are created to take advantage of [D3]'s data binding
capabilities.

[D3] is undoubtedly a powerful visualization library, _but_ it only works in JavaScript environments
(namely, the Web).

Enter Krayon, a [Kotlin]&trade; [multiplatform] library that aims to bring powerful visualization
tools to the many platforms supported by [Kotlin]&trade; (e.g. Android, iOS, Web).

## Elements

Since Android and iOS don't have an HTML [DOM], Krayon provides intermediary [`Element`]s
that can undergo data binding and be rendered on supported platforms.

```mermaid
graph LR;
    Data --> Elements
    subgraph Krayon
        Elements
        SvgKanvas
        subgraph Web
            HtmlKanvas
        end
        subgraph Android
            AndroidKanvas
            ComposeKanvas
        end
        subgraph iOS
            CGContextKanvas
        end
    end
    Elements --> HtmlKanvas
    Elements --> AndroidKanvas
    Elements --> SvgKanvas
    Elements --> ComposeKanvas
    Elements --> CGContextKanvas
    style Krayon fill:#b3e5fc
    style Web fill:#e8d44d
    style Android fill:#9fc137
    style iOS fill:#adadad
```
<br/>

## Drawing

To draw a simple line on an HTML [Canvas], we start by creating a [Canvas]:

```html
<canvas id="canvas1" width="100" height="100" style="outline: black 1px solid;"></canvas>
```

We can then perform the following in [Kotlin]&trade;:

1. Create a [`RootElement`]
2. Access [`RootElement`] as a [`Selection`]
3. Add a [`LineElement`] to the [`RootElement`]
4. Update the start and end points of the [`LineElement`]
5. Draw the [`RootElement`] to a [`HtmlKanvas`] (which bridges to an HTML [Canvas]) 

```kotlin
{% include kotlin/tutorial/Line1.kt %}
```

We can simply call into the JavaScript API produced by Kotlin (via `@JsExport`):

```html
<script>sample.tutorial.setupLine1(document.getElementById("canvas1"));</script>
```

This will render as:

<canvas id="canvas1" width="100" height="100" style="outline: black 1px solid;"></canvas>
<script>sample.tutorial.setupLine1(document.getElementById("canvas1"));</script>

### Data

To draw the same line as above, but powered by a dataset, we can perform the following in
[Kotlin]&trade;:

1. Create a [`RootElement`]
2. Access [`RootElement`] as a [`Selection`]
3. Selects all [`LineElement`]s that are children of the [`RootElement`]
4. Associate data with the [`Selection`]
5. Combine each element of the data with a [`LineElement`]
6. Iterate over each item of the data (`data` is a `Pair<Point, Point>`) and [`LineElement`]s
7. Deconstruct `data` as `start` (`Point`) and `end` (`Point`) 
8. Assign the `Point` values to the [`LineElement`] properties (`startX`, `startY`, etc.)
9. Draw the [`RootElement`] to a [`HtmlKanvas`] (which bridges to an HTML [Canvas])

```kotlin
{% include kotlin/tutorial/Line2.kt %}
```

This will render as:

<canvas id="canvas2" width="100" height="100" style="outline: black 1px solid;"></canvas>
<script>sample.tutorial.setupLine2(document.getElementById("canvas2"));</script>

### Bar Chart

We can use a larger dataset with the [`LineElement`] to create a simple bar chart. We start by
creating a larger HTML [Canvas]:

```html
<canvas id="barchart" width="250" height="100" style="outline: black 1px solid;"></canvas>
```

Then we can construct the bar chart in [Kotlin]&trade;:

1. Extract the `width` and `height` of the HTML [Canvas] as `Float`s
2. Create an example dataset (1 through 10, incrementing by 1)
3. Configure scaling on the x-axis (essentially, mapping dataset indices to the width of the [Canvas])
4. Configure scaling on the y-axis (mapping the range of dataset values to the height of the [Canvas])<sup>1</sup>
5. Configure the color and width of the bars
6. Create a [`RootElement`]
7. Access [`RootElement`] as a [`Selection`]
8. Selects all [`LineElement`]s that are children of the [`RootElement`]
9. Associate data with the [`Selection`]
10. Combine each element of the data with a [`LineElement`]
11. Iterate over each item of the data (indexed)
12. Assign scaled values to the [`LineElement`] properties (`startX`, `startY`, etc.)
13. Assign the bar paint (from step 5)
14. Draw the [`RootElement`] to a [`HtmlKanvas`] (which bridges to an HTML [Canvas])

<sup>1</sup> Note that for step 4 the order of the `range` values are flipped (with `height` listed
before `0f`). This is to invert y-axis rendering, since the rendering origin is the upper left
corner (with y increasing downward).

```kotlin
{% include kotlin/tutorial/BarChart.kt %}
```

This will render as:

<canvas id="barchart" width="250" height="100" style="outline: black 1px solid;"></canvas>
<script>sample.tutorial.setupBarChart(document.getElementById("barchart"));</script>

[Canvas]: https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API
[D3]: https://d3js.org/
[DOM]: https://en.wikipedia.org/wiki/Document_Object_Model
[Document Object Model]: https://en.wikipedia.org/wiki/Document_Object_Model
[Kotlin]: https://kotlinlang.org/
[`Element`]: https://juullabs.github.io/api/krayon/element/com.juul.krayon.element/-element/index.html
[`HtmlKanvas`]: https://juullabs.github.io/krayon/kanvas/com.juul.krayon.kanvas/-html-kanvas/index.html
[`LineElement`]: https://juullabs.github.io/api/krayon/element/com.juul.krayon.element/-line-element/index.html
[`RootElement`]: https://juullabs.github.io/api/krayon/element/com.juul.krayon.element/-root-element/index.html
[`Selection`]: https://juullabs.github.io/api/krayon/selection/com.juul.krayon.selection/-selection/index.html
[multiplatform]: https://kotlinlang.org/docs/multiplatform.html
