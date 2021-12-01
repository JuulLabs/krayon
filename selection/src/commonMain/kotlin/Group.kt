package com.juul.krayon.selection

import com.juul.krayon.element.Element

public data class Group<E : Element, D>(
    public val parent: Element?,
    public val nodes: List<E?>,
)
