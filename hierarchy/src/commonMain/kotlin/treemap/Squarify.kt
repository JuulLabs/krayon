package com.juul.krayon.hierarchy.treemap

import com.juul.krayon.hierarchy.Node

private const val GOLDEN_RATIO = 1.61803398875f

/** Implementation of https://www.win.tue.nl/~vanwijk/stm.pdf */
public class Squarify(
    private val aspectRatio: Float = GOLDEN_RATIO,
) : TileMethod {

    init {
        require(aspectRatio >= 1f) { "Aspect ratio cannot be less than 1, but was `$aspectRatio`." }
    }

    override fun tile(parent: Node<*, Tile>) {
        var i0 = 0
        var i1 = 0
        var remainingTile = parent.layout
        var remainingWeight = parent.weight
        while (i0 < parent.children.size) {
            val width = remainingTile.width
            val height = remainingTile.height

            // Find a non-empty node
            var sumWeight: Float
            do {
                sumWeight = parent.children[i1++].weight
            } while (sumWeight == 0f && i1 < parent.children.size)

            // Calculate the initial state from that node
            var minWeight = sumWeight
            var maxWeight = sumWeight
            val alpha = maxOf(height / width, width / height) / (remainingWeight * aspectRatio)
            var beta = sumWeight * sumWeight * alpha
            var minRatio = maxOf(maxWeight / beta, beta / minWeight)

            // Keep checking nodes until the fit is worse
            while (i1 < parent.children.size) {
                val nodeWeight = parent.children[i1].weight
                sumWeight += nodeWeight
                if (nodeWeight < minWeight) minWeight = nodeWeight
                if (nodeWeight > maxWeight) maxWeight = nodeWeight
                beta = sumWeight * sumWeight * alpha
                val newRatio = maxOf(maxWeight / beta, beta / minWeight)
                if (newRatio > minRatio) {
                    sumWeight -= nodeWeight
                    break
                } else {
                    minRatio = newRatio
                    i1 += 1
                }
            }

            // Position the nodes
            val fillTile = if (width < height) {
                remainingTile.copy(bottom = remainingTile.top + height * (sumWeight / remainingWeight))
            } else {
                remainingTile.copy(right = remainingTile.left + width * (sumWeight / remainingWeight))
            }
            val dummy = Node(parent.data, fillTile).also {
                it.weight = sumWeight
                @Suppress("UNCHECKED_CAST")
                it.children = parent.children.subList(i0, i1) as List<Node<Any?, Tile>>
            }
            val method = if (width < height) Dice else Slice
            method.tile(dummy)
            remainingWeight -= sumWeight
            remainingTile = if (width < height) {
                remainingTile.copy(top = fillTile.bottom)
            } else {
                remainingTile.copy(left = fillTile.right)
            }
            i0 = i1
        }
    }
}
