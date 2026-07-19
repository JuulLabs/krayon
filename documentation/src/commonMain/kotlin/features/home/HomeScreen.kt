package com.juul.krayon.documentation.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.Screen
import com.juul.krayon.documentation.components.DataToDomDiagram
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.features.gallery.SineWaveDemo

@Composable
fun HomeScreen(onNavigate: (Screen) -> Unit) {
    Column {
        Text("What is Krayon?", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            **Krayon** is a free, open-source [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
            library for **visualizing data**. Heavily inspired by [D3](https://d3js.org/) — and mirroring its API
            where possible — Krayon brings D3's low-level, data-driven approach to every platform Kotlin reaches:
            **Android, iOS, desktop (JVM & macOS), and the web** (JS and Wasm).

            The chart below is being drawn by Krayon right now, at your display's frame rate, inside this
            Compose for Web application:
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 260.dp) {
            SineWaveDemo(Modifier.fillMaxWidth().height(240.dp))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            Button(onClick = { onNavigate(Screen.GettingStarted) }) {
                Text("Get started")
            }
            OutlinedButton(onClick = { onNavigate(Screen.Gallery) }) {
                Text("Browse the gallery")
            }
        }

        Text("Data-driven documents, without the documents", style = MaterialTheme.typography.h5, modifier = Modifier.padding(top = 24.dp))
        MarkdownBlock(
            """
            > D3 (which stands for *Data-Driven Documents*) is commonly used to have a dataset drive
            > manipulation of the HTML Document Object Model (DOM).

            [D3's "most central feature" is "data binding"](https://stackoverflow.com/a/50143500):
            data drives the creation, mutation, and removal of DOM elements. D3 is undoubtedly a
            powerful visualization library, *but* it only works in JavaScript environments — namely,
            the web.

            Android and iOS don't have an HTML DOM, so Krayon provides its own lightweight
            [`Element`](https://juullabs.github.io/krayon/api/element/com.juul.krayon.element/-element/index.html)
            tree that plays the DOM's role: data binds to elements, and the resulting element tree is
            rendered to whatever canvas the platform provides.
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DataToDomDiagram()
        MarkdownBlock(
            """
            A chart in Krayon is an ordinary Kotlin function of its data:

            ```kotlin
            fun barChart(root: RootElement, width: Float, height: Float, data: List<Float>) {
                val x = scale().domain(0, data.size - 1).range(0f, width)
                val y = scale().domain(0f, data.max()).range(height, 0f)

                root.asSelection()
                    .selectAll(LineElement)
                    .data(data)
                    .join(LineElement)
                    .each { (value, index) ->
                        startX = x.scale(index)
                        endX = x.scale(index)
                        startY = y.scale(0f)
                        endY = y.scale(value)
                        paint = Paint.Stroke(steelBlue, x.scale(1))
                    }
            }
            ```

            Call it again with new data and the element tree updates in place — that one property
            makes charts resizable, animatable, and interactive for free.

            ## Where to go next

            * **Getting started** — add the dependency and draw your first chart.
            * **Concepts** — guided tours of each module: selections, scales, shapes, axes, hierarchy, color, rendering, and interaction.
            * **Krayon for D3 users** — a translation table from D3 idioms to Krayon.
            * **Gallery** — live, interactive examples with complete source code.
            * **API reference** — Dokka documentation for every published module.
            """,
            Modifier.padding(vertical = 8.dp),
        )
        Spacer(Modifier.width(1.dp))
    }
}
