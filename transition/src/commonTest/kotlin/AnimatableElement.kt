package com.juul.krayon.transition

import com.juul.krayon.color.Color
import com.juul.krayon.color.black
import com.juul.krayon.element.Element
import com.juul.krayon.element.ElementBuilder
import com.juul.krayon.element.ElementSelector
import com.juul.krayon.kanvas.Kanvas

/** A minimal element exposing one property of each interpolatable type, for exercising [attribute]. */
class AnimatableElement : Element() {

    override val tag: String get() = "animatable"

    var floatValue: Float by attributes.withDefault { 0f }
    var doubleValue: Double by attributes.withDefault { 0.0 }
    var intValue: Int by attributes.withDefault { 0 }
    var color: Color by attributes.withDefault { black }

    override fun draw(kanvas: Kanvas) = Unit

    companion object : ElementBuilder<AnimatableElement>, ElementSelector<AnimatableElement> {
        override fun build(): AnimatableElement = AnimatableElement()

        override fun trySelect(element: Element): AnimatableElement? = element as? AnimatableElement
    }
}
