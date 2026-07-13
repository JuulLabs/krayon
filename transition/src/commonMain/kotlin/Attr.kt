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
 * Animates the [property] of each selected element towards a constant [target] value. The starting
 * value is read from each element when the transition starts, so re-created transitions interrupt and
 * resume smoothly from the current value.
 *
 * This is the type-safe analogue of d3's [transition.attr](https://d3js.org/d3-transition/modifying#transition_attr):
 * where d3 uses a string attribute name, Krayon uses a strongly-typed property reference.
 */
@JvmName("attrFloat")
public fun <E : Element, D> Transition<E, D>.attr(
    property: KMutableProperty1<E, Float>,
    target: Float,
): Transition<E, D> = attr(property) { target }

/** Animates the [property] towards a target computed per element from its datum and index. */
@JvmName("attrFloatOf")
public fun <E : Element, D> Transition<E, D>.attr(
    property: KMutableProperty1<E, Float>,
    target: E.(Arguments<D, E?>) -> Float,
): Transition<E, D> = animateAttr(property, target) { start, end -> interpolator(start, end) }

/** Animates a [Double] [property] towards a constant [target]. */
@JvmName("attrDouble")
public fun <E : Element, D> Transition<E, D>.attr(
    property: KMutableProperty1<E, Double>,
    target: Double,
): Transition<E, D> = attr(property) { target }

/** Animates a [Double] [property] towards a target computed per element. */
@JvmName("attrDoubleOf")
public fun <E : Element, D> Transition<E, D>.attr(
    property: KMutableProperty1<E, Double>,
    target: E.(Arguments<D, E?>) -> Double,
): Transition<E, D> = animateAttr(property, target) { start, end -> interpolator(start, end) }

/** Animates an [Int] [property] towards a constant [target]. */
@JvmName("attrInt")
public fun <E : Element, D> Transition<E, D>.attr(
    property: KMutableProperty1<E, Int>,
    target: Int,
): Transition<E, D> = attr(property) { target }

/** Animates an [Int] [property] towards a target computed per element. */
@JvmName("attrIntOf")
public fun <E : Element, D> Transition<E, D>.attr(
    property: KMutableProperty1<E, Int>,
    target: E.(Arguments<D, E?>) -> Int,
): Transition<E, D> = animateAttr(property, target) { start, end -> interpolator(start, end) }

/** Animates a [Color] [property] towards a constant [target], interpolating in ARGB space. */
@JvmName("attrColor")
public fun <E : Element, D> Transition<E, D>.attr(
    property: KMutableProperty1<E, Color>,
    target: Color,
): Transition<E, D> = attr(property) { target }

/** Animates a [Color] [property] towards a target computed per element. */
@JvmName("attrColorOf")
public fun <E : Element, D> Transition<E, D>.attr(
    property: KMutableProperty1<E, Color>,
    target: E.(Arguments<D, E?>) -> Color,
): Transition<E, D> = animateAttr(property, target) { start, end -> interpolator(start, end) }

private inline fun <E : Element, D, V> Transition<E, D>.animateAttr(
    property: KMutableProperty1<E, V>,
    crossinline target: E.(Arguments<D, E?>) -> V,
    crossinline interpolatorFor: (start: V, end: V) -> Interpolator<V>,
): Transition<E, D> {
    Selection(groups).each { arguments ->
        val element = this
        val end = target(arguments)
        scheduler.scheduleFor(element, id)?.tweens?.set("attr.${property.name}") {
            val interpolate = interpolatorFor(property.get(element), end)
            val applier: (Float) -> Unit = { fraction -> property.set(element, interpolate.interpolate(fraction)) }
            applier
        }
    }
    return this
}
