package com.juul.krayon.documentation.features.d3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.components.MarkdownBlock

@Composable
fun D3Screen() {
    Column {
        Text("Krayon for D3 users", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            Krayon deliberately mirrors [D3](https://d3js.org/)'s API where Kotlin allows. If you
            know D3, you already know most of Krayon — this page maps the idioms across.

            ## The big difference

            D3 binds data to the browser's DOM. Krayon binds data to its own multiplatform
            [`Element` tree](#concepts/rendering), then renders that tree to a platform canvas
            (Compose, Android, HTML canvas, Core Graphics, or SVG). Everything else about the
            mental model — selections, joins, scales, shapes, axes — carries over.

            ## Module map

            | D3 module | Krayon module | Notes |
            |---|---|---|
            | d3-selection | `selection` | near 1-to-1 |
            | d3-scale | `scale` | continuous scales only (linear & time) |
            | d3-shape | `shape` | line, area, pie, arc |
            | d3-axis | `axis` | four edges, configurable ticks |
            | d3-hierarchy | `hierarchy` | hierarchy + treemap |
            | d3-color | `color` | ARGB colors, named constants, lerp |
            | d3-interpolate | `interpolate` | numbers, time, colors |
            | d3-time | `time` | calendar intervals for time ticks |
            | DOM | `element` | the multiplatform stand-in |
            | Canvas/SVG | `kanvas` | pluggable render backends |
            | — | `compose` | Compose bridge (`ElementView`) |
            | — | `box` | aggregate of all published modules |

            ## Phrase book

            | D3 | Krayon |
            |---|---|
            | `d3.select(node)` | `element.asSelection()` |
            | `selection.selectAll("circle")` | `selection.selectAll(CircleElement)` |
            | `selection.selectAll(".tick")` | `selection.selectAll(Element.withKind("tick"))` |
            | `selection.data(data)` | `selection.data(data)` |
            | `selection.data(data, key)` | `selection.keyedData(data) { key }` |
            | `selection.join("circle")` | `selection.join(CircleElement)` |
            | `selection.attr("cx", d => x(d))` | `selection.each { (d) -> centerX = x.scale(d) }` |
            | `d3.scaleLinear().domain([0, 1]).range([0, w])` | `scale().domain(0f, 1f).range(0f, w)` |
            | `d3.scaleTime()` | `scale().domain(instants)` |
            | `d3.extent(data, accessor)` | `data.extent { accessor }` |
            | `d3.line().x(...).y(...)` | `line<D>().x { ... }.y { ... }` |
            | `line.defined(d => d != null)` | `null` data, or `line.defined { ... }` |
            | `d3.area().y0(h).y1(...)` | `area<D>().y0(h).y1 { ... }` |
            | `d3.pie().value(...)` | `pie().value<D> { ... }` |
            | `d3.arc().innerRadius(r)` | `arc(outerRadius, innerRadius)` |
            | `d3.axisLeft(y)` | `axisLeft(y)` |
            | `g.call(axis)` | `selection.call(axis)` |
            | `axis.ticks(5)` | `axis.apply { tickCount = 5 }` |
            | `axis.tickFormat(f)` | `axis.apply { formatter = f }` |
            | `d3.hierarchy(data).sum(...)` | `hierarchy(data) { children }.sum { ... }` |
            | `d3.treemap().size([w, h])` | `Treemap(w, h)` + `layoutWith` |
            | `d3.interpolateRgb(a, b)(t)` | `lerp(a, b, t)` |
            | `selection.on("click", f)` | `element.onClick { ... }` |

            ## D3 examples ported to Krayon

            The [gallery](#gallery) reproduces these classics from the
            [D3 gallery](https://observablehq.com/@d3/gallery), each with full source:
            bar chart, horizontal bar chart, stacked bar chart, histogram, line chart,
            line chart with missing data, multi-series line chart, area chart, scatterplot,
            pie chart, donut chart, treemap, and color legends — plus Krayon-native examples of
            animation and pointer interaction.

            ## Not (yet) in Krayon

            Reaching for one of these? It isn't available yet — contributions welcome:

            * **Scales:** band, ordinal, log, pow, quantile (use index math for bar charts)
            * **Shapes:** curves beyond `Linear`, stacks, symbols, links, radial variants
            * **Layouts:** force simulation, pack, partition/sunburst, tree, cluster, chord, sankey
            * **Geo:** projections, paths, shapes (d3-geo)
            * **Analysis:** voronoi/delaunay, contours, bins (use `groupBy`)
            * **Behaviors:** zoom, brush, drag
            * **Transitions:** d3-transition-style tweens (drive values from your data source instead)
            * **Formatting:** d3-format / d3-time-format (use Kotlin string templates)
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}
