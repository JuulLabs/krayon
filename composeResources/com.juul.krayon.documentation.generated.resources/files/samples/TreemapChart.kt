package com.juul.krayon.documentation.samples

import com.juul.krayon.box.setShapeFrom
import com.juul.krayon.color.Color
import com.juul.krayon.color.cornflowerBlue
import com.juul.krayon.color.crimson
import com.juul.krayon.color.goldenrod
import com.juul.krayon.color.lerp
import com.juul.krayon.color.seaGreen
import com.juul.krayon.color.white
import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.hierarchy.hierarchy
import com.juul.krayon.hierarchy.isLeaf
import com.juul.krayon.hierarchy.sum
import com.juul.krayon.hierarchy.traverseBreadthFirst
import com.juul.krayon.hierarchy.treemap.SliceAndDice
import com.juul.krayon.hierarchy.treemap.Squarify
import com.juul.krayon.hierarchy.treemap.TileMethod
import com.juul.krayon.hierarchy.treemap.Treemap
import com.juul.krayon.hierarchy.treemap.layoutWith
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll

data class Package(
    val name: String,
    val size: Float = 0f,
    val children: List<Package> = emptyList(),
)

/** A miniature take on D3's classic "flare" package-size dataset. */
val packageSizes: Package = Package(
    "krayon",
    children = listOf(
        Package(
            "vis",
            children = listOf(
                Package("axis", 24f),
                Package("scale", 30f),
                Package("shape", 44f),
                Package("selection", 36f),
            ),
        ),
        Package(
            "draw",
            children = listOf(
                Package("kanvas", 58f),
                Package("element", 40f),
                Package("compose", 26f),
            ),
        ),
        Package(
            "data",
            children = listOf(
                Package("hierarchy", 22f),
                Package("interpolate", 12f),
                Package("color", 16f),
                Package("time", 14f),
            ),
        ),
    ),
)

data class TreemapChart(
    val data: Package = packageSizes,
    val tileMethod: TileMethod = Squarify(),
)

/** Tiling with [SliceAndDice] instead of the default [Squarify]. */
fun sliceAndDiceTreemap(): TreemapChart = TreemapChart(tileMethod = SliceAndDice)

private val branchColors = listOf(cornflowerBlue, seaGreen, goldenrod, crimson)
private val labelPaint = Paint.Text(white, 11f, Paint.Text.Alignment.Center, Font(sansSerif))

fun treemapChart(root: RootElement, width: Float, height: Float, chart: TreemapChart) {
    // Build the hierarchy, weigh it, then lay it out as tiles.
    val layout = hierarchy(chart.data) { it.children }
        .sum { it.size }
        .layoutWith(Treemap(width, height, chart.tileMethod, paddingInner = { 2f }))

    // Color leaves by their top-level branch.
    val branchIndex = chart.data.children
        .mapIndexed { index, branch -> branch.name to index }
        .toMap()

    val leaves = layout.traverseBreadthFirst()
        .filter { it.isLeaf }
        .map { node ->
            val branch = generateSequence(node) { it.parent }.first { it.parent?.parent == null }
            Triple(node.data, node.layout, branchIndex.getOrElse(branch.data.name) { 0 })
        }
        .toList()

    root.asSelection()
        .selectAll(RectangleElement)
        .data(leaves)
        .join(RectangleElement)
        .each { (leaf) ->
            val (_, tile, branch) = leaf
            setShapeFrom(tile)
            paint = Paint.Fill(lerp(branchColors[branch % branchColors.size], white, 0.15f))
        }

    root.asSelection()
        .selectAll(TextElement)
        .data(leaves.filter { (_, tile, _) -> tile.width > 48f && tile.height > 20f })
        .join(TextElement)
        .each { (leaf) ->
            val (pkg, tile, _) = leaf
            text = pkg.name
            x = tile.centerX
            y = tile.centerY + 4f
            paint = labelPaint
        }
}
