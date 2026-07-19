package com.juul.krayon.hierarchy

/** Cartesian coordinate attached as a layout by [com.juul.krayon.hierarchy.tree.Tree] and [com.juul.krayon.hierarchy.cluster.Cluster]. */
public class Point internal constructor(
    public val x: Float,
    public val y: Float,
) {
    override fun toString(): String = "Point(x=$x, y=$y)"
}
