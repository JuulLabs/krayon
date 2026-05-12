package com.juul.krayon.selection

/**
 * Wrapper around d3's standard argument order. This helps serve as syntax sugar for their style of overloading,
 * where you often don't use the arguments at all, or only bind the first one or two.
 *
 * Library consumers should not hold on to instances of this class, it is internally mutated heavily as an optimization.
 */
public class Arguments<D, T> private constructor(
    datum: D,
    index: Int,
    data: List<T>,
) {
    public var datum: D = datum
        internal set
    public var index: Int = index
        internal set
    public var data: List<T> = data
        internal set

    public operator fun component1(): D = datum

    public operator fun component2(): Int = index

    public operator fun component3(): List<T> = data

    @PublishedApi
    internal class Buffer<D, T> {
        private val arguments = Arguments<D?, T>(null, -1, listOf())

        operator fun invoke(datum: D, index: Int, data: List<T>): Arguments<D, T> {
            arguments.datum = datum
            arguments.index = index
            arguments.data = data
            @Suppress("UNCHECKED_CAST")
            return arguments as Arguments<D, T>
        }
    }
}
