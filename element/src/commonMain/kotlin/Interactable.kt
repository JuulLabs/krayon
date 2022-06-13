package com.juul.krayon.element

import com.juul.krayon.kanvas.Path

public interface Interactable<T : Interactable<T>> {
    public var onClick: ((T) -> Unit)?
    public fun getInteractionPath(): Path
}
