package com.juul.krayon.selection

import com.juul.krayon.element.Element

/**
 * Wrapper around d3's standard argument order. This helps serve as syntax sugar for their style of overloading,
 * where you often don't use the arguments at all, or only bind the first one or two.
 */
public data class Arguments<E : Element, D>(
    public val datum: D,
    public val index: Int,
    public val group: List<E?>,
)
