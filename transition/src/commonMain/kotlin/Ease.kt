package com.juul.krayon.transition

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * An easing function distorts the normalized time of a transition to control apparent motion.
 * Given a [fraction] in the range `0f..1f`, it returns an eased fraction (also typically in
 * `0f..1f`, though some easings overshoot). A well-behaved easing returns `0f` at `0f` and `1f`
 * at `1f`.
 *
 * See the analogous [d3-ease](https://d3js.org/d3-ease) module.
 */
public fun interface Easing {
    public fun ease(fraction: Float): Float
}

private const val HALF_PI: Float = (PI / 2).toFloat()
private const val TAU: Float = (2 * PI).toFloat()

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeLinear). */
public val easeLinear: Easing = Easing { it }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easePolyIn). */
public fun easePolynomialIn(exponent: Float = 3f): Easing = Easing { t -> t.pow(exponent) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easePolyOut). */
public fun easePolynomialOut(exponent: Float = 3f): Easing = Easing { t -> 1f - (1f - t).pow(exponent) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easePolyInOut). */
public fun easePolynomialInOut(exponent: Float = 3f): Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        t.pow(exponent) / 2f
    } else {
        (2f - (2f - t).pow(exponent)) / 2f
    }
}

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeQuadIn). */
public val easeQuadraticIn: Easing = Easing { t -> t * t }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeQuadOut). */
public val easeQuadraticOut: Easing = Easing { t -> t * (2f - t) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeQuadInOut). */
public val easeQuadraticInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        t * t / 2f
    } else {
        val u = t - 1f
        (1f - u * (u - 2f)) / 2f
    }
}

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeCubicIn). */
public val easeCubicIn: Easing = Easing { t -> t * t * t }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeCubicOut). */
public val easeCubicOut: Easing = Easing { fraction ->
    val t = fraction - 1f
    t * t * t + 1f
}

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeCubicInOut). */
public val easeCubicInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        t * t * t / 2f
    } else {
        val u = t - 2f
        (u * u * u + 2f) / 2f
    }
}

/** Alias for [easeCubicInOut]; the default easing of [transition]. */
public val easeCubic: Easing = easeCubicInOut

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeSinIn). */
public val easeSinusoidalIn: Easing = Easing { t -> if (t == 1f) 1f else 1f - cos(t * HALF_PI) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeSinOut). */
public val easeSinusoidalOut: Easing = Easing { t -> sin(t * HALF_PI) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeSinInOut). */
public val easeSinusoidalInOut: Easing = Easing { t -> (1f - cos(PI.toFloat() * t)) / 2f }

/** Normalized `2^(-10t)` so that `tpmt(0) == 1` and `tpmt(1) == 0` (matches d3-ease). */
private fun tpmt(x: Float): Float {
    val raw = 2.0.pow(-10.0 * x) - 0.0009765625
    return (raw * 1.0009775171065494).toFloat()
}

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeExpIn). */
public val easeExponentialIn: Easing = Easing { t -> tpmt(1f - t) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeExpOut). */
public val easeExponentialOut: Easing = Easing { t -> 1f - tpmt(t) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeExpInOut). */
public val easeExponentialInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        tpmt(1f - t) / 2f
    } else {
        (2f - tpmt(t - 1f)) / 2f
    }
}

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeCircleIn). */
public val easeCircularIn: Easing = Easing { t -> 1f - sqrt(1f - t * t) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeCircleOut). */
public val easeCircularOut: Easing = Easing { fraction ->
    val t = fraction - 1f
    sqrt(1f - t * t)
}

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeCircleInOut). */
public val easeCircularInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        (1f - sqrt(1f - t * t)) / 2f
    } else {
        val u = t - 2f
        (sqrt(1f - u * u) + 1f) / 2f
    }
}

private const val DEFAULT_AMPLITUDE: Float = 1f
private const val DEFAULT_PERIOD: Float = 0.3f

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeElasticIn). */
public fun easeElasticIn(amplitude: Float = DEFAULT_AMPLITUDE, period: Float = DEFAULT_PERIOD): Easing {
    val a = maxOf(1f, amplitude)
    val p = period / TAU
    val s = asin(1f / a) * p
    return Easing { fraction ->
        val t = fraction - 1f
        a * tpmt(-t) * sin((s - t) / p)
    }
}

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeElasticOut). */
public fun easeElasticOut(amplitude: Float = DEFAULT_AMPLITUDE, period: Float = DEFAULT_PERIOD): Easing {
    val a = maxOf(1f, amplitude)
    val p = period / TAU
    val s = asin(1f / a) * p
    return Easing { t -> 1f - a * tpmt(t) * sin((t + s) / p) }
}

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeElasticInOut). */
public fun easeElasticInOut(amplitude: Float = DEFAULT_AMPLITUDE, period: Float = DEFAULT_PERIOD): Easing {
    val a = maxOf(1f, amplitude)
    val p = period / TAU
    val s = asin(1f / a) * p
    return Easing { fraction ->
        val t = fraction * 2f - 1f
        if (t < 0f) {
            a * tpmt(-t) * sin((s - t) / p) / 2f
        } else {
            (2f - a * tpmt(t) * sin((s + t) / p)) / 2f
        }
    }
}

private const val DEFAULT_OVERSHOOT: Float = 1.70158f

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeBackIn). */
public fun easeBackIn(overshoot: Float = DEFAULT_OVERSHOOT): Easing =
    Easing { t -> t * t * (overshoot * (t - 1f) + t) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeBackOut). */
public fun easeBackOut(overshoot: Float = DEFAULT_OVERSHOOT): Easing =
    Easing { fraction ->
        val t = fraction - 1f
        t * t * ((t + 1f) * overshoot + t) + 1f
    }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeBackInOut). */
public fun easeBackInOut(overshoot: Float = DEFAULT_OVERSHOOT): Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t < 1f) {
        t * t * ((overshoot + 1f) * t - overshoot) / 2f
    } else {
        val u = t - 2f
        (u * u * ((overshoot + 1f) * u + overshoot) + 2f) / 2f
    }
}

private const val B1: Float = 4f / 11f
private const val B2: Float = 6f / 11f
private const val B3: Float = 8f / 11f
private const val B4: Float = 3f / 4f
private const val B5: Float = 9f / 11f
private const val B6: Float = 10f / 11f
private const val B7: Float = 15f / 16f
private const val B8: Float = 21f / 22f
private const val B9: Float = 63f / 64f
private val B0: Float = 1f / B1 / B1

private fun bounceOut(t: Float): Float = when {
    t < B1 -> B0 * t * t
    t < B3 -> {
        val u = t - B2
        B0 * u * u + B4
    }
    t < B6 -> {
        val u = t - B5
        B0 * u * u + B7
    }
    else -> {
        val u = t - B8
        B0 * u * u + B9
    }
}

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeBounceIn). */
public val easeBounceIn: Easing = Easing { t -> 1f - bounceOut(1f - t) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeBounceOut). */
public val easeBounceOut: Easing = Easing { t -> bounceOut(t) }

/** See analogous [d3 easing](https://d3js.org/d3-ease#easeBounceInOut). */
public val easeBounceInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) (1f - bounceOut(1f - t)) / 2f else (bounceOut(t - 1f) + 1f) / 2f
}
