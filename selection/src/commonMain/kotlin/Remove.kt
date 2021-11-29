package com.juul.krayon.selection

import com.juul.krayon.element.Element

public fun <E : Element, D, S : Selection<E, D>> S.remove(): S {
    return this.each { parent?.removeChild(this) }
}
