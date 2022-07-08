package com.juul.krayon.kanvas

public class Path internal constructor(
    private val instructions: SegmentedPath,
) {

    public constructor(
        path: PathBuilder<*>.() -> Unit,
    ) : this(SegmentedPathBuilder().apply(path).build())

    private val cache = mutableMapOf<PathTypeMarker<*>, Any>()

    /**
     * Bridge the gap between this platform agnostic type and its platform specific type. The
     * platform specific type is cached inside this [Path] instance, and multiple calls with the
     * same marker will return the same object. As such, it is _very important_ that you don't
     * mutate the returned platform type.
     */
    public fun <T : Any> get(marker: PathTypeMarker<T>): T {
        @Suppress("UNCHECKED_CAST")
        return cache.getOrPut(marker) { instructions.rebuildWith(marker.builder) } as T
    }
}
