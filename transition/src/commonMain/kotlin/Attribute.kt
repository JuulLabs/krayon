package com.juul.krayon.transition

import com.juul.krayon.color.Color
import com.juul.krayon.element.Element
import com.juul.krayon.interpolate.Interpolator
import com.juul.krayon.interpolate.interpolator
import com.juul.krayon.selection.Arguments
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.each
import kotlin.jvm.JvmName
import kotlin.reflect.KMutableProperty1

/**
 * Animates the [property] of each selected element towards a [target] value. The starting value is
 * read from each element when the transition starts, so re-created transitions interrupt and resume
 * smoothly from the current value.
 *
 * This is the type-safe analogue of [d3's transition.attr](https://d3js.org/d3-transition/modifying#transition_attr):
 * where d3 uses a string attribute name, Krayon uses a property reference.
 */
@JvmName("attributeFloat")
public fun <E : Element, D> Transition<E, D>.attribute(
    property: KMutableProperty1<E, Float>,
    target: Float,
): Transition<E, D> = attribute(property) { target }

/** See analogous [d3 function](https://d3js.org/d3-transition/modifying#transition_attr). */
@JvmName("attributeFloatOf")
public fun <E : Element, D> Transition<E, D>.attribute(
    property: KMutableProperty1<E, Float>,
    target: E.(Arguments<D, E?>) -> Float,
): Transition<E, D> = animateAttribute(property, target) { start, end -> interpolator(start, end) }

/** See analogous [d3 function](https://d3js.org/d3-transition/modifying#transition_attr). */
@JvmName("attributeDouble")
public fun <E : Element, D> Transition<E, D>.attribute(
    property: KMutableProperty1<E, Double>,
    target: Double,
): Transition<E, D> = attribute(property) { target }

/** See analogous [d3 function](https://d3js.org/d3-transition/modifying#transition_attr). */
@JvmName("attributeDoubleOf")
public fun <E : Element, D> Transition<E, D>.attribute(
    property: KMutableProperty1<E, Double>,
    target: E.(Arguments<D, E?>) -> Double,
): Transition<E, D> = animateAttribute(property, target) { start, end -> interpolator(start, end) }

/** See analogous [d3 function](https://d3js.org/d3-transition/modifying#transition_attr). */
@JvmName("attributeInt")
public fun <E : Element, D> Transition<E, D>.attribute(
    property: KMutableProperty1<E, Int>,
    target: Int,
): Transition<E, D> = attribute(property) { target }

/** See analogous [d3 function](https://d3js.org/d3-transition/modifying#transition_attr). */
@JvmName("attributeIntOf")
public fun <E : Element, D> Transition<E, D>.attribute(
    property: KMutableProperty1<E, Int>,
    target: E.(Arguments<D, E?>) -> Int,
): Transition<E, D> = animateAttribute(property, target) { start, end -> interpolator(start, end) }

/** See analogous [d3 function](https://d3js.org/d3-transition/modifying#transition_attr). */
@JvmName("attributeColor")
public fun <E : Element, D> Transition<E, D>.attribute(
    property: KMutableProperty1<E, Color>,
    target: Color,
): Transition<E, D> = attribute(property) { target }

/** See analogous [d3 function](https://d3js.org/d3-transition/modifying#transition_attr). */
@JvmName("attributeColorOf")
public fun <E : Element, D> Transition<E, D>.attribute(
    property: KMutableProperty1<E, Color>,
    target: E.(Arguments<D, E?>) -> Color,
): Transition<E, D> = animateAttribute(property, target) { start, end -> interpolator(start, end) }

private inline fun <E : Element, D, V> Transition<E, D>.animateAttribute(
    property: KMutableProperty1<E, V>,
    crossinline target: E.(Arguments<D, E?>) -> V,
    crossinline interpolatorFor: (start: V, end: V) -> Interpolator<V>,
): Transition<E, D> {
    Selection(groups).each { arguments ->
        val element = this
        val end = target(arguments)
        scheduler.scheduleFor(element, id)?.tweens?.set("attribute.${property.name}") {
            val interpolate = interpolatorFor(property.get(element), end)
            val applier: (Float) -> Unit = { fraction -> property.set(element, interpolate.interpolate(fraction)) }
            applier
        }
    }
    return this
}
