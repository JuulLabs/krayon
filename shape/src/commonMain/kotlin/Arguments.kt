package com.juul.krayon.shape

/**
 * Library consumers should not hold on to instances of this
 * class, it is internally mutated heavily as an optimization.
 */
public class Arguments<D : Any> internal constructor(
    datum: D,
    index: Int,
    data: List<D?>,
) {
    public var datum: D = datum
        internal set
    public var index: Int = index
        internal set
    public var data: List<D?> = data
        internal set

    public operator fun component1(): D = datum
    public operator fun component2(): Int = index
    public operator fun component3(): List<D?> = data
}
