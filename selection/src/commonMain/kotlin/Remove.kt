package com.juul.krayon.selection

import com.juul.krayon.element.Element

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_remove). */
public fun <E : Element, D, S : Selection<E, D>> S.remove(): S =
    each { parent?.removeChild(this) }
