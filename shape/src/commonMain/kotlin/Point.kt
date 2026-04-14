package com.juul.krayon.shape

/** Represents a point in 2D space with x and y coordinates. */
public data class Point(val x: Float, val y: Float)

/** Extracts the x-coordinate from pairs, lists, or bare numbers. */
internal val pointX: (Arguments<*>) -> Float = { (datum) ->
    when (datum) {
        is Point -> datum.x
        is Pair<*, *> -> (datum.first as Number).toFloat()
        is List<*> -> (datum[0] as Number).toFloat()
        is Number -> datum.toFloat()
        else -> 0f
    }
}

/** Extracts the y-coordinate from pairs, lists, or bare numbers. */
internal val pointY: (Arguments<*>) -> Float = { (datum) ->
    when (datum) {
        is Point -> datum.y
        is Pair<*, *> -> (datum.second as Number).toFloat()
        is List<*> -> (datum[1] as Number).toFloat()
        is Number -> datum.toFloat()
        else -> 0f
    }
}
