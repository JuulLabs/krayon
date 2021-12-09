package com.juul.krayon.selection

/**
 * Wrapper around d3's standard argument order. This helps serve as syntax sugar for their style of overloading,
 * where you often don't use the arguments at all, or only bind the first one or two.
 */
public data class Arguments<D, T>(
    public val datum: D,
    public val index: Int,
    public val group: List<T>,
)
