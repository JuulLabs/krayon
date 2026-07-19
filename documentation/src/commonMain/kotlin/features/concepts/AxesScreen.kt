package com.juul.krayon.documentation.features.concepts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.call
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.components.ValueSlider
import com.juul.krayon.documentation.samples.hourlyTemperature
import com.juul.krayon.documentation.samples.lineChart
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll

/** Configurable axis properties, bound to the sliders below. */
private data class AxisPlayground(
    val tickCount: Int,
    val tickSizeInner: Float,
    val tickPadding: Float,
)

/** A single bottom axis over `domain(0, 1000)`, restyled live from [AxisPlayground]. */
private fun axisPlaygroundChart(root: RootElement, width: Float, height: Float, config: AxisPlayground) {
    val margin = 32f
    val x = scale()
        .domain(0f, 1000f)
        .range(0f, width - margin * 2f)

    root.asSelection()
        .selectAll(TransformElement.withKind("x-axis"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "x-axis" } }
        .each { transform = Transform.Translate(horizontal = margin, vertical = height / 2f) }
        .call(
            axisBottom(x).apply {
                tickCount = config.tickCount
                tickSizeInner = config.tickSizeInner
                tickPadding = config.tickPadding
            },
        )
}

@Composable
fun AxesScreen() {
    Column {
        Text("Axes", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            *The Krayon equivalent of [d3-axis](https://d3js.org/d3-axis).*

            An axis renders human-readable reference marks for a scale: a domain line, ticks, and
            labels. One factory per chart edge — `axisTop`, `axisRight`, `axisBottom`, `axisLeft` —
            each accepting a scale whose range is in pixels.

            ```kotlin
            body.selectAll(GroupElement.withKind("y-axis"))
                .data(listOf(null))
                .join { append(GroupElement).each { kind = "y-axis" } }
                .call(axisLeft(y))
            ```

            `call(axis)` builds the axis elements *inside* the selected group. Axes don't position
            themselves: translate the containing group to the edge it belongs on:

            ```kotlin
            body.selectAll(TransformElement.withKind("x-axis"))
                .data(listOf(null))
                .join { append(TransformElement).each { kind = "x-axis" } }
                .each { transform = Transform.Translate(vertical = innerHeight) }
                .call(axisBottom(x))
            ```
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 280.dp, caption = "axisBottom with an hour formatter; axisLeft with a degree formatter.") {
            ElementView({ hourlyTemperature }, ::lineChart, Modifier.fillMaxSize())
        }
        MarkdownBlock(
            """
            ## Configuration

            Everything about an axis is a mutable property — try the common ones live:
            """,
            Modifier.padding(vertical = 8.dp),
        )
        AxisPlaygroundDemo()
        MarkdownBlock(
            """
            Set them in an `apply` block:

            ```kotlin
            axisBottom(x).apply {
                tickCount = 6                              // approximate; nice values win
                formatter = { hour -> "${'$'}{hour.toInt()}:00" }  // label text
                tickSizeInner = 6f                         // tick line length
                tickSizeOuter = 6f                         // domain end caps
                tickPadding = 3f                           // gap between tick and label
                textSize = 14f
                textColor = black
                lineWidth = 1f
                lineColor = black
                font = Font(sansSerif)
            }
            ```

            ## Time axes

            Axis factories are overloaded for `Float`, `Double`, `Instant`, and `LocalDateTime`
            scales. Time axes tick on calendar boundaries (from the `time` module's intervals), so
            ticks land on midnights, month starts, and other sensible instants — see the gallery's
            [multi-series line chart](#gallery/multi-series-line-chart).

            ## A word of caution: selections inside axes

            An axis creates `PathElement`s, `LineElement`s, and `TextElement`s inside its group.
            Because selections search all descendants, scope your own joins with
            [kinds](#concepts/selections) (e.g. `PathElement.withKind("line")`) so they don't
            accidentally capture the axis's internals.
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun AxisPlaygroundDemo() {
    val tickCount = remember { mutableStateOf(5f) }
    val tickSize = remember { mutableStateOf(6f) }
    val tickPadding = remember { mutableStateOf(3f) }
    val config = remember {
        derivedStateOf {
            AxisPlayground(
                tickCount = tickCount.value.toInt(),
                tickSizeInner = tickSize.value,
                tickPadding = tickPadding.value,
            )
        }
    }
    DemoCard(height = 240.dp, caption = "Note how the ticker rounds the tick count to whatever produces nice values.") {
        Column(Modifier.fillMaxSize()) {
            ElementView(config, ::axisPlaygroundChart, Modifier.fillMaxWidth().weight(1f))
            ValueSlider("Tick count", 1f, 20f, tickCount)
            ValueSlider("Tick size", 0f, 24f, tickSize)
            ValueSlider("Tick padding", 0f, 16f, tickPadding)
        }
    }
}
