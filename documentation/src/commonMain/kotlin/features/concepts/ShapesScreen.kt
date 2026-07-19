package com.juul.krayon.documentation.features.concepts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.color.steelBlue
import com.juul.krayon.color.white
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.samples.Point
import com.juul.krayon.documentation.samples.areaChart
import com.juul.krayon.documentation.samples.browserShares
import com.juul.krayon.documentation.samples.monthlyRainfall
import com.juul.krayon.documentation.samples.pieChart
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import com.juul.krayon.shape.line
import kotlin.random.Random

/** The essence of a shape generator: a handful of points in, one `Path` out. */
private fun pointsToPath(root: RootElement, width: Float, height: Float, points: List<Point>) {
    val toPath = line<Point>()
        .x { (point) -> point.x * width }
        .y { (point) -> point.y * height }

    root.asSelection()
        .selectAll(PathElement.withKind("line"))
        .data(listOf(points))
        .join {
            append(PathElement).each {
                kind = "line"
                paint = Paint.Stroke(steelBlue, 2f)
            }
        }
        .each { (data) -> path = toPath.render(data) }

    root.asSelection()
        .selectAll(CircleElement)
        .data(points)
        .join {
            append(CircleElement).each {
                radius = 4f
                paint = Paint.FillAndStroke(Paint.Fill(white), Paint.Stroke(steelBlue, 2f))
            }
        }
        .each { (point) ->
            centerX = point.x * width
            centerY = point.y * height
        }
}

private fun randomPoints(): List<Point> = List(7) { index ->
    Point(x = (index + 0.5f) / 7f, y = Random.nextFloat() * 0.8f + 0.1f)
}

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

            That's the whole idea — points in, `Path` out:
            """,
            Modifier.padding(vertical = 8.dp),
        )
        PointsToPathDemo()
        MarkdownBlock(
            """
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

@Composable
private fun PointsToPathDemo() {
    val points = remember { mutableStateOf(randomPoints()) }
    DemoCard(height = 220.dp, caption = "line().render(points) — the circles are the data, the path is the generator's output.") {
        Column(Modifier.fillMaxSize()) {
            ElementView(points, ::pointsToPath, Modifier.fillMaxWidth().weight(1f).padding(8.dp))
            OutlinedButton(onClick = { points.value = randomPoints() }) {
                Text("New points")
            }
        }
    }
}
