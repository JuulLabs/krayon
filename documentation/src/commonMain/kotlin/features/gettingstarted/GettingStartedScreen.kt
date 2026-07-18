package com.juul.krayon.documentation.features.gettingstarted

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.samples.barChart
import com.juul.krayon.documentation.samples.letterFrequency

@Composable
fun GettingStartedScreen() {
    Column {
        Text("Getting started", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            ## Installation

            Krayon is published to Maven Central under the `com.juul.krayon` group. The `box`
            artifact re-exports every published module — the easiest way to get started:

            ```kotlin
            commonMain.dependencies {
                implementation("com.juul.krayon:box:${'$'}version")
            }
            ```

            If your app uses Compose (Multiplatform or Android), also add the Compose bridge:

            ```kotlin
            commonMain.dependencies {
                implementation("com.juul.krayon:compose:${'$'}version")
            }
            ```

            Individual modules (`core`, `kanvas`, `element`, `selection`, `scale`, `shape`, `axis`,
            `hierarchy`, `interpolate`, `color`, `time`, `element-view`) can be added à la carte
            instead, if you prefer a minimal dependency set.

            ## Pick a render target

            Chart code is common Kotlin. Only the last step — putting pixels on screen — is
            platform-specific:

            | Platform | Entry point | Module |
            |---|---|---|
            | Compose (Android, iOS, desktop, web) | `ElementView(data, ::chart)` composable | `compose` |
            | Android `View` | `ElementView` / `ElementViewAdapter` | `element-view` |
            | Browser (Kotlin/JS) | `root.draw(HtmlKanvas(canvas))` | `kanvas` |
            | iOS / macOS | `root.draw(CGContextKanvas(context))` | `kanvas` |
            | SVG (any platform) | `root.draw(SvgKanvas(width, height))` | `kanvas` |

            ## Your first chart

            Let's draw a bar chart of letter frequency in English text. A Krayon chart is a
            function that fills a [`RootElement`](https://juullabs.github.io/krayon/api/element/com.juul.krayon.element/-root-element/index.html)
            based on the current size and data:

            1. **Margins** — reserve space for the axes around the plot area.
            2. **Scales** — map data values (domain) to pixel positions (range). Note the y-range is
               `height, 0` — flipped, because the drawing origin is the *top*-left corner.
            3. **Select & join** — `selectAll(RectangleElement)` + `data(...)` + `join(...)` pairs
               each datum with an element, creating and removing elements as needed.
            4. **Update** — `each { }` writes datum-derived attributes onto every element.
            5. **Axes** — `call(axisLeft(y))` renders reference ticks into a group.

            ```kotlin
            fun barChart(root: RootElement, width: Float, height: Float, data: List<Pair<String, Float>>) {
                val margin = Margin(top = 16f, right = 8f, bottom = 24f, left = 40f)   // 1
                val innerWidth = width - margin.left - margin.right
                val innerHeight = height - margin.top - margin.bottom

                val bandWidth = innerWidth / data.size                                 // 2
                val y = scale()                                                        // 2
                    .domain(0f, data.maxOf { (_, frequency) -> frequency })            // 2
                    .range(innerHeight, 0f)                                            // 2

                val body = root.asSelection()
                    .selectAll(TransformElement.withKind("body"))
                    .data(listOf(null))
                    .join { append(TransformElement).each { kind = "body" } }
                    .each { transform = Transform.Translate(margin.left, margin.top) }

                body.selectAll(GroupElement.withKind("y-axis"))                        // 5
                    .data(listOf(null))
                    .join { append(GroupElement).each { kind = "y-axis" } }
                    .call(axisLeft(y))

                body.selectAll(RectangleElement)                                       // 3
                    .data(data)                                                        // 3
                    .join(RectangleElement)                                            // 3
                    .each { (datum, index) ->                                          // 4
                        val (_, frequency) = datum
                        left = index * bandWidth + bandWidth * 0.1f
                        right = (index + 1) * bandWidth - bandWidth * 0.1f
                        top = y.scale(frequency)
                        bottom = innerHeight
                        paint = Paint.Fill(steelBlue)
                    }
            }
            ```

            ## Put it on screen

            With Compose, hand the chart function and its data to `ElementView`:

            ```kotlin
            @Composable
            fun LetterFrequencyChart() {
                ElementView(
                    deriveData = { letterFrequency },
                    updateElements = ::barChart,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                )
            }
            ```

            The result — live, and resizable with your browser window:
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 300.dp) {
            ElementView({ letterFrequency }, ::barChart, Modifier.fillMaxSize())
        }
        MarkdownBlock(
            """
            `ElementView` re-invokes the chart function whenever the size or data changes. Because
            the data join updates elements in place, the same function handles first render, resize,
            new data, and (as the gallery shows) animation and interaction.

            On the web without Compose, the same chart function draws onto a plain HTML canvas:

            ```kotlin
            @JsExport
            fun setupBarChart(canvas: HTMLCanvasElement) {
                val root = RootElement()
                barChart(root, canvas.width.toFloat(), canvas.height.toFloat(), letterFrequency)
                root.draw(HtmlKanvas(canvas))
            }
            ```

            ## Next steps

            Head to the **Concepts** section for guided tours of each module, or browse the
            **Gallery** to crib from complete, working examples.
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}
