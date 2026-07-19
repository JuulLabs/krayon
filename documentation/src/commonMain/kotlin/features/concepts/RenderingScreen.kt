package com.juul.krayon.documentation.features.concepts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.juul.krayon.color.crimson
import com.juul.krayon.color.lightSteelBlue
import com.juul.krayon.color.steelBlue
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.components.ClassStructureDiagram
import com.juul.krayon.documentation.components.CodeBlock
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.LineElement
import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.svg.SvgKanvas
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll

/** A tiny scene used to demonstrate that one element tree renders to any Kanvas. */
private fun miniScene(root: RootElement, width: Float, height: Float, data: Unit) {
    root.asSelection()
        .selectAll(RectangleElement)
        .data(listOf(data))
        .join(RectangleElement)
        .each {
            left = width * 0.08f
            top = height * 0.15f
            right = width * 0.42f
            bottom = height * 0.7f
            paint = Paint.Fill(lightSteelBlue)
        }

    root.asSelection()
        .selectAll(CircleElement)
        .data(listOf(data))
        .join(CircleElement)
        .each {
            centerX = width * 0.68f
            centerY = height * 0.42f
            radius = minOf(width, height) * 0.22f
            paint = Paint.Fill(steelBlue)
        }

    root.asSelection()
        .selectAll(LineElement)
        .data(listOf(data))
        .join(LineElement)
        .each {
            startX = width * 0.08f
            startY = height * 0.85f
            endX = width * 0.92f
            endY = height * 0.85f
            paint = Paint.Stroke(crimson, 2f)
        }
}

/** Renders [miniScene] through [SvgKanvas], producing SVG markup. */
private fun miniSceneAsSvg(width: Float, height: Float): String {
    val root = RootElement()
    miniScene(root, width, height, Unit)
    val kanvas = SvgKanvas(width, height)
    root.draw(kanvas)
    return kanvas.build()
}

@Composable
fun RenderingScreen() {
    Column {
        Text("Elements & rendering", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            Where D3 mutates the browser's DOM, Krayon mutates its own retained tree of
            `Element`s — the piece that makes the D3 model portable to platforms without a DOM.

            ## The element tree

            Elements are lightweight, mutable nodes. Structural elements organize the tree, and
            drawable elements put geometry on screen:

            | Element | Draws |
            |---|---|
            | `RootElement` | nothing — the tree's root |
            | `GroupElement` | nothing — a plain container |
            | `TransformElement` | children, under translate/scale/rotate/skew |
            | `ClipElement` | children, clipped to a path |
            | `LineElement` | a line segment |
            | `RectangleElement` / `RoundedRectangleElement` | rectangles |
            | `CircleElement` | a circle |
            | `PathElement` | arbitrary `Path` geometry (from shape generators or by hand) |
            | `TextElement` | a run of text |
            | `KanvasElement` | custom drawing via raw `Kanvas` calls |

            Every drawable element carries a `paint` — fills, strokes (with caps, joins, dashes),
            gradients, or text styling — defined in the `kanvas` module.

            ## Kanvas: one drawing API, many canvases

            `Kanvas` is Krayon's multiplatform canvas interface, deliberately shaped like Android's
            `Canvas` and the browser's `CanvasRenderingContext2D`. Drawing an element tree is one
            call — `root.draw(kanvas)` — against any implementation:
            """,
            Modifier.padding(vertical = 8.dp),
        )
        ClassStructureDiagram()
        MarkdownBlock(
            """
            | Kanvas | Platform | Module |
            |---|---|---|
            | `ComposeKanvas` | Compose Multiplatform (Android, iOS, desktop, web) | `compose` |
            | `AndroidKanvas` / `KanvasView` | Android `Canvas` / `View` | `kanvas` |
            | `HtmlKanvas` | Browser `<canvas>` (Kotlin/JS) | `kanvas` |
            | `CGContextKanvas` | iOS & macOS Core Graphics | `kanvas` |
            | `SvgKanvas` | SVG markup string (any platform) | `kanvas` |

            ## Compose: ElementView

            For Compose apps, `ElementView` owns the render loop: it re-runs your chart function
            when the size or data changes, requests frames for `Flow`-driven data, and dispatches
            pointer events into the tree:

            ```kotlin
            ElementView(
                dataSource = dataFlow,        // Flow<T>, State<T>, or () -> T
                updateElements = ::lineChart, // (RootElement, width, height, T) -> Unit
                modifier = Modifier.fillMaxSize(),
            )
            ```

            ## Android Views

            The `element-view` module has an Android-native `ElementView` that renders off the main
            thread through a bitmap pool — useful outside Compose.

            ## One tree, many canvases — live

            The scene below is a three-element tree. On the left it renders through
            `ComposeKanvas` (what you're looking at); on the right, the *same tree* was drawn
            through `SvgKanvas`, and the markup you see was generated live on this page:

            ```kotlin
            val svg = SvgKanvas(width, height)
            root.draw(svg)
            val markup = svg.build()
            ```
            """,
            Modifier.padding(vertical = 8.dp),
        )
        SvgExportDemo()
    }
}

@Composable
private fun SvgExportDemo() {
    DemoCard(height = 200.dp, caption = "The same RootElement, rendered by ComposeKanvas.") {
        ElementView({ }, ::miniScene, Modifier.fillMaxSize())
    }
    val markup = remember { miniSceneAsSvg(width = 280f, height = 140f) }
    CodeBlock(AnnotatedString(markup), Modifier.padding(vertical = 8.dp))
    Text(
        text = "The same RootElement, rendered by SvgKanvas at 280×140.",
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
    )
}
