package com.juul.krayon.selection

import com.juul.krayon.element.Element

public data class Group<T>(
    public val parent: Element?,
    public val nodes: List<Element?>
)
