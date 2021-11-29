package com.juul.krayon.selection

import com.juul.krayon.element.Element

public data class Arguments<E : Element, D>(
    public val datum: D,
    public val index: Int,
    public val group: List<E?>,
)
