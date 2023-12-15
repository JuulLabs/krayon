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
    style Krayon fill:#B3E5FC
    style Web fill:#E8D44D
    style Android fill:#9FC137
    style iOS fill:#ADADAD
```
<br/>

## Drawing

To draw a simple line on an HTML [Canvas], we start by creating a [Canvas]:

```html
<canvas id="canvas1" width="100" height="100" style="outline: black 1px solid;"></canvas>
```

We can then perform the following in [Kotlin]&trade;:

1. Create a [`RootElement`]
2. Access [`RootElement`] as a [`Selection`] (which allows for attribute modifications)
3. Add a [`LineElement`] to the [`RootElement`]
4. Update the start and end points of the [`LineElement`]
5. Draw the [`RootElement`] to a [`HtmlKanvas`] which bridges to an HTML [Canvas] 

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

To draw the same line as above, powered by a dataset, we can perform the following in
[Kotlin]&trade;:

1. Create a [`RootElement`]
2. Access [`RootElement`] as a [`Selection`] (which allows for attribute modifications)
3. Add a [`LineElement`] to the [`RootElement`]
4. Update the start and end points of the [`LineElement`]
5. Draw the [`RootElement`] to a [`HtmlKanvas`] which bridges to an HTML [Canvas]

```kotlin
{% include kotlin/tutorial/Line2.kt %}
```

This will render as:

<canvas id="canvas2" width="100" height="100" style="outline: black 1px solid;"></canvas>
<script>sample.tutorial.setupLine2(document.getElementById("canvas2"));</script>


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
