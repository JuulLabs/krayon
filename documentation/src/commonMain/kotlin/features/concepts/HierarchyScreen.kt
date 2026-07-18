package com.juul.krayon.documentation.features.concepts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.features.gallery.TreemapDemo

@Composable
fun HierarchyScreen() {
    Column {
        Text("Hierarchy", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            *The Krayon equivalent of [d3-hierarchy](https://d3js.org/d3-hierarchy).*

            The `hierarchy` module turns nested data into a tree of `Node`s, computes aggregate
            weights over it, and lays it out for display.

            ## Building a hierarchy

            `hierarchy` wraps any recursive structure, given a way to find children;
            `flatHierarchy` wraps a flat list under a synthetic root:

            ```kotlin
            val tree = hierarchy(packageSizes) { it.children }

            val flat = flatHierarchy(letterCounts.entries)
            ```

            ## Weighing nodes

            Layouts need to know how much space each node deserves. `sum` computes each node's
            weight as its own value plus its descendants'; `count` weighs each leaf equally, and
            `sort` reorders children:

            ```kotlin
            val weighted = tree.sum { it.size }
            ```

            Traversal helpers (`each`, `eachBefore`, `eachAfter`, `ancestors`, `traverse*`) walk
            the tree in the same orders as their D3 counterparts.

            ## Treemap layout

            `Treemap` assigns each node a rectangular `Tile`. Two tiling strategies are built in:
            `Squarify` (D3's default — aims for square-ish tiles) and `SliceAndDice` (alternates
            direction per depth). Toggle between them below:

            ```kotlin
            val layout = hierarchy(packageSizes) { it.children }
                .sum { it.size }
                .layoutWith(Treemap(width, height, Squarify(), paddingInner = { 2f }))
            ```
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 360.dp) {
            TreemapDemo(Modifier.fillMaxSize(), withControls = true)
        }
        MarkdownBlock(
            """
            After layout, every node's `layout` property is a `Tile` with `left`/`top`/`right`/
            `bottom` (plus `width`, `height`, and center helpers). The `box` module's
            `setShapeFrom(tile)` copies a tile straight onto a `RectangleElement`:

            ```kotlin
            root.asSelection()
                .selectAll(RectangleElement)
                .data(layout.traverseBreadthFirst().filter { it.isLeaf }.toList())
                .join(RectangleElement)
                .each { (node) -> setShapeFrom(node.layout) }
            ```

            For flat data, `removeHierarchy()` strips the synthetic root and pairs each datum with
            its tile — see the gallery's [interactive treemap](#gallery/interactive-treemap).

            ## Differences from D3

            Treemaps are the only layout so far: partition/sunburst, pack, tree, and cluster
            layouts are not yet available.
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}
