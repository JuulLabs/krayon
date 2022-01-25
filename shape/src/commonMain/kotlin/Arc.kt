package com.juul.krayon.shape

import com.juul.krayon.kanvas.Path

public fun arc(
    outerRadius: Float,
    innerRadius: Float = 0f,
): Arc = Arc(outerRadius, innerRadius)

public class Arc internal constructor(
    outerRadius: Float,
    innerRadius: Float,
) {
    public var outerRadius: Float = outerRadius
        private set
    public var innerRadius: Float = innerRadius
        private set
    public var cornerRadius: Float = 0f
        private set
    public var padRadius: Float? = null
        private set

    public fun outerRadius(value: Float): Arc = apply { outerRadius = value }
    public fun innerRadius(value: Float): Arc = apply { innerRadius = value }
    public fun cornerRadius(value: Float): Arc = apply { cornerRadius = value }
    public fun padRadius(value: Float?): Arc = apply { padRadius = value }

    public operator fun invoke(startAngle: Float, endAngle: Float, padAngle: Float): Path {
        TODO()
    }

    public operator fun invoke(slice: Slice<*>): Path = this(slice.startAngle, slice.endAngle, slice.padAngle)
}
