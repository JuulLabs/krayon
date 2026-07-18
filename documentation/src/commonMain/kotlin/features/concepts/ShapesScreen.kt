package com.juul.krayon.documentation.features.concepts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.juul.krayon.documentation.samples.areaChart
import com.juul.krayon.documentation.samples.browserShares
import com.juul.krayon.documentation.samples.monthlyRainfall
import com.juul.krayon.documentation.samples.pieChart

@Composable
fun ShapesScreen() {
    Column {
        Text("Shapes", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            *The Krayon equivalent of [d3-shape](https://d3js.org/d3-shape).*

            Shape generators turn data into `Path`s — the geometry of lines, areas, and pies —
            leaving *where* and *how* to draw them to you.

            ## Line

            `line()` connects points. Configure how a datum maps to x and y, then `render` a
            dataset:

            ```kotlin
            val temperatureLine = line<Point>()
                .x { (point) -> x.scale(point.x) }
                .y { (point) -> y.scale(point.y) }

            pathElement.path = temperatureLine.render(data)
            ```

            `null` data (or a `defined` predicate) splits the line into segments — see
            [Line chart, missing data](#gallery/missing-data) in the gallery.

            ## Area

            `area()` fills between two boundaries: a topline (`x1`/`y1`) and a baseline
            (`x0`/`y0`). Fixed values work anywhere an accessor does:

            ```kotlin
            val rainfallArea = area<Point>()
                .x { (point) -> x.scale(point.x) }
                .y0(innerHeight)                       // flat baseline
                .y1 { (point) -> y.scale(point.y) }    // data topline
            ```
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 260.dp, caption = "area() + line() over the same data.") {
            ElementView({ monthlyRainfall }, ::areaChart, Modifier.fillMaxSize())
        }
        MarkdownBlock(
            """
            ## Pie & arc

            Division of labor, exactly as in D3: `pie()` computes angles from values, and `arc()`
            turns angles into wedge geometry.

            ```kotlin
            val sharePie = pie().value<BrowserShare> { it.share }
            val shareArc = arc(outerRadius = radius)

            selection.data(sharePie(data))    // List<Slice<BrowserShare>>
                .join(PathElement)
                .each { (slice) -> path = shareArc(slice) }
            ```

            Angles are radians; `0` points at 12 o'clock and increases clockwise. The arc generator
            handles inner radius (donuts), corner rounding, and pad angles:

            ```kotlin
            arc(outerRadius = radius, innerRadius = radius * 0.5f)
                .cornerRadius(4f)
            ```
            """,
            Modifier.padding(vertical = 8.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DemoCard(height = 260.dp, modifier = Modifier.weight(1f), caption = "pie() + arc() with centroid labels.") {
                ElementView({ browserShares }, ::pieChart, Modifier.fillMaxSize())
            }
        }
        MarkdownBlock(
            """
            ## Curves

            Line and area accept a `curve` controlling interpolation between points. `Linear` is
            currently the only built-in curve; the `Curve` interface is open for custom
            implementations, and more built-ins (monotone, basis, step) are natural future
            additions.

            ## Differences from D3

            Radial lines/areas, links, symbols, and stack generators are not yet available. Stacks
            are a few lines of Kotlin by hand — see the gallery's
            [stacked bar chart](#gallery/stacked-bar-chart).
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}
