package com.juul.krayon.documentation.features.concepts

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
import com.juul.krayon.documentation.samples.colorScales

@Composable
fun ScalesScreen() {
    Column {
        Text("Scales", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            *The Krayon equivalent of [d3-scale](https://d3js.org/d3-scale).*

            Scales map an abstract dimension of data (the **domain**) to a visual dimension (the
            **range**) — most commonly, values to pixels.

            ```kotlin
            val y = scale()               // ContinuousScale<Float, Float>, [0,1] → [0,1]
                .domain(0f, 100f)         // data values...
                .range(innerHeight, 0f)   // ...to pixels (flipped: the origin is top-left)

            y.scale(50f)                  // == innerHeight / 2
            ```

            ## Domains

            Domains work over any `Comparable` type with a built-in interpolator: `Int`, `Float`,
            `Double`, and — for time series — `Instant`, `LocalDate`, and `LocalDateTime`:

            ```kotlin
            val x = scale()
                .domain(LocalDateTime(2024, 1, 1, 0, 0), LocalDateTime(2024, 12, 1, 0, 0))
                .range(0f, innerWidth)
            ```

            Domains (and ranges) may have more than two values, splitting the scale into segments —
            the basis of diverging scales. They can also be computed from data with the `extent`
            helper (D3's `d3.extent`):

            ```kotlin
            val x = scale()
                .domain(data.extent { it.x })   // [min, max] of the data
                .range(0f, innerWidth)
            ```

            `min`, `max`, and `nice` helpers round out the toolbox.

            ## Ranges beyond pixels

            A range can be any type with an interpolator — including `Color`. Each ramp below is a
            `ContinuousScale<Float, Color>` sampled 64 times:
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 240.dp) {
            ElementView({ }, ::colorScales, Modifier.fillMaxSize())
        }
        MarkdownBlock(
            """
            ```kotlin
            val color = scale()
                .domain(0f, 0.5f, 1f)
                .range(listOf(mediumBlue, white, crimson))   // diverging color scale

            color.scale(0.25f)   // a soft blue
            ```

            ## Ticks

            Scales cooperate with tickers to produce human-friendly tick values for axes: `Float`
            and `Double` tickers pick round numbers, while the time tickers (for `Instant` and
            `LocalDateTime`) walk calendar intervals from the `time` module — seconds up to years.
            You rarely use tickers directly; the `axis` module does it for you.

            ## Differences from D3

            Krayon currently provides the equivalent of `d3.scaleLinear` and `d3.scaleTime` (via
            domain types). Band, ordinal, log, and power scales don't exist yet — for bar charts,
            compute positions with index math (see the gallery's bar charts).
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}
