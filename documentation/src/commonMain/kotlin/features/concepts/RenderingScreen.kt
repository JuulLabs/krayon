package com.juul.krayon.documentation.features.concepts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.generated.resources.class_structure
import org.jetbrains.compose.resources.painterResource

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
        Image(
            painter = painterResource(Res.drawable.class_structure),
            contentDescription = "Element tree renders through Kanvas implementations on each platform",
            contentScale = FixedScale(2f),
            modifier = Modifier.padding(vertical = 8.dp),
        )
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

            ## SVG export

            `SvgKanvas` turns any chart into SVG markup — handy for server-side rendering or
            export features:

            ```kotlin
            val svg = SvgKanvas(width, height)
            root.draw(svg)
            val markup = svg.build()
            ```
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}
