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

/** Linear easing; the identity function. See [d3.easeLinear](https://d3js.org/d3-ease#easeLinear). */
public val easeLinear: Easing = Easing { it }

// region Polynomial

/** Polynomial easing-in with the given [exponent]. See [d3.easePolyIn](https://d3js.org/d3-ease#easePolyIn). */
public fun easePolynomialIn(exponent: Float = 3f): Easing = Easing { t -> t.pow(exponent) }

/** Polynomial easing-out with the given [exponent]. See [d3.easePolyOut](https://d3js.org/d3-ease#easePolyOut). */
public fun easePolynomialOut(exponent: Float = 3f): Easing = Easing { t -> 1f - (1f - t).pow(exponent) }

/** Symmetric polynomial easing with the given [exponent]. See [d3.easePolyInOut](https://d3js.org/d3-ease#easePolyInOut). */
public fun easePolynomialInOut(exponent: Float = 3f): Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        t.pow(exponent) / 2f
    } else {
        (2f - (2f - t).pow(exponent)) / 2f
    }
}

// endregion

// region Quadratic

/** Quadratic easing-in. See [d3.easeQuadIn](https://d3js.org/d3-ease#easeQuadIn). */
public val easeQuadraticIn: Easing = Easing { t -> t * t }

/** Quadratic easing-out. See [d3.easeQuadOut](https://d3js.org/d3-ease#easeQuadOut). */
public val easeQuadraticOut: Easing = Easing { t -> t * (2f - t) }

/** Symmetric quadratic easing. See [d3.easeQuadInOut](https://d3js.org/d3-ease#easeQuadInOut). */
public val easeQuadraticInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        t * t / 2f
    } else {
        val u = t - 1f
        (1f - u * (u - 2f)) / 2f
    }
}

// endregion

// region Cubic

/** Cubic easing-in. See [d3.easeCubicIn](https://d3js.org/d3-ease#easeCubicIn). */
public val easeCubicIn: Easing = Easing { t -> t * t * t }

/** Cubic easing-out. See [d3.easeCubicOut](https://d3js.org/d3-ease#easeCubicOut). */
public val easeCubicOut: Easing = Easing { fraction ->
    val t = fraction - 1f
    t * t * t + 1f
}

/** Symmetric cubic easing. See [d3.easeCubicInOut](https://d3js.org/d3-ease#easeCubicInOut). */
public val easeCubicInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        t * t * t / 2f
    } else {
        val u = t - 2f
        (u * u * u + 2f) / 2f
    }
}

/** Alias for [easeCubicInOut]; the default transition easing. See [d3.easeCubic](https://d3js.org/d3-ease#easeCubic). */
public val easeCubic: Easing = easeCubicInOut

// endregion

// region Sinusoidal

/** Sinusoidal easing-in. See [d3.easeSinIn](https://d3js.org/d3-ease#easeSinIn). */
public val easeSinusoidalIn: Easing = Easing { t -> if (t == 1f) 1f else 1f - cos(t * HALF_PI) }

/** Sinusoidal easing-out. See [d3.easeSinOut](https://d3js.org/d3-ease#easeSinOut). */
public val easeSinusoidalOut: Easing = Easing { t -> sin(t * HALF_PI) }

/** Symmetric sinusoidal easing. See [d3.easeSinInOut](https://d3js.org/d3-ease#easeSinInOut). */
public val easeSinusoidalInOut: Easing = Easing { t -> (1f - cos(PI.toFloat() * t)) / 2f }

// endregion

// region Exponential

/** Normalized `2^(-10t)` so that `tpmt(0) == 1` and `tpmt(1) == 0` (matches d3-ease). */
private fun tpmt(x: Float): Float {
    val raw = 2.0.pow(-10.0 * x) - 0.0009765625
    return (raw * 1.0009775171065494).toFloat()
}

/** Exponential easing-in. See [d3.easeExpIn](https://d3js.org/d3-ease#easeExpIn). */
public val easeExponentialIn: Easing = Easing { t -> tpmt(1f - t) }

/** Exponential easing-out. See [d3.easeExpOut](https://d3js.org/d3-ease#easeExpOut). */
public val easeExponentialOut: Easing = Easing { t -> 1f - tpmt(t) }

/** Symmetric exponential easing. See [d3.easeExpInOut](https://d3js.org/d3-ease#easeExpInOut). */
public val easeExponentialInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        tpmt(1f - t) / 2f
    } else {
        (2f - tpmt(t - 1f)) / 2f
    }
}

// endregion

// region Circular

/** Circular easing-in. See [d3.easeCircleIn](https://d3js.org/d3-ease#easeCircleIn). */
public val easeCircularIn: Easing = Easing { t -> 1f - sqrt(1f - t * t) }

/** Circular easing-out. See [d3.easeCircleOut](https://d3js.org/d3-ease#easeCircleOut). */
public val easeCircularOut: Easing = Easing { fraction ->
    val t = fraction - 1f
    sqrt(1f - t * t)
}

/** Symmetric circular easing. See [d3.easeCircleInOut](https://d3js.org/d3-ease#easeCircleInOut). */
public val easeCircularInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) {
        (1f - sqrt(1f - t * t)) / 2f
    } else {
        val u = t - 2f
        (sqrt(1f - u * u) + 1f) / 2f
    }
}

// endregion

// region Elastic

private const val DEFAULT_AMPLITUDE: Float = 1f
private const val DEFAULT_PERIOD: Float = 0.3f

/** Elastic easing-in. See [d3.easeElasticIn](https://d3js.org/d3-ease#easeElasticIn). */
public fun easeElasticIn(amplitude: Float = DEFAULT_AMPLITUDE, period: Float = DEFAULT_PERIOD): Easing {
    val a = maxOf(1f, amplitude)
    val p = period / TAU
    val s = asin(1f / a) * p
    return Easing { fraction ->
        val t = fraction - 1f
        a * tpmt(-t) * sin((s - t) / p)
    }
}

/** Elastic easing-out. See [d3.easeElasticOut](https://d3js.org/d3-ease#easeElasticOut). */
public fun easeElasticOut(amplitude: Float = DEFAULT_AMPLITUDE, period: Float = DEFAULT_PERIOD): Easing {
    val a = maxOf(1f, amplitude)
    val p = period / TAU
    val s = asin(1f / a) * p
    return Easing { t -> 1f - a * tpmt(t) * sin((t + s) / p) }
}

/** Symmetric elastic easing. See [d3.easeElasticInOut](https://d3js.org/d3-ease#easeElasticInOut). */
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

// endregion

// region Back

private const val DEFAULT_OVERSHOOT: Float = 1.70158f

/** Anticipatory (back) easing-in. See [d3.easeBackIn](https://d3js.org/d3-ease#easeBackIn). */
public fun easeBackIn(overshoot: Float = DEFAULT_OVERSHOOT): Easing =
    Easing { t -> t * t * (overshoot * (t - 1f) + t) }

/** Anticipatory (back) easing-out. See [d3.easeBackOut](https://d3js.org/d3-ease#easeBackOut). */
public fun easeBackOut(overshoot: Float = DEFAULT_OVERSHOOT): Easing =
    Easing { fraction ->
        val t = fraction - 1f
        t * t * ((t + 1f) * overshoot + t) + 1f
    }

/** Symmetric anticipatory (back) easing. See [d3.easeBackInOut](https://d3js.org/d3-ease#easeBackInOut). */
public fun easeBackInOut(overshoot: Float = DEFAULT_OVERSHOOT): Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t < 1f) {
        t * t * ((overshoot + 1f) * t - overshoot) / 2f
    } else {
        val u = t - 2f
        (u * u * ((overshoot + 1f) * u + overshoot) + 2f) / 2f
    }
}

// endregion

// region Bounce

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

/** Bounce easing-in. See [d3.easeBounceIn](https://d3js.org/d3-ease#easeBounceIn). */
public val easeBounceIn: Easing = Easing { t -> 1f - bounceOut(1f - t) }

/** Bounce easing-out. See [d3.easeBounceOut](https://d3js.org/d3-ease#easeBounceOut). */
public val easeBounceOut: Easing = Easing { t -> bounceOut(t) }

/** Symmetric bounce easing. See [d3.easeBounceInOut](https://d3js.org/d3-ease#easeBounceInOut). */
public val easeBounceInOut: Easing = Easing { fraction ->
    val t = fraction * 2f
    if (t <= 1f) (1f - bounceOut(1f - t)) / 2f else (bounceOut(t - 1f) + 1f) / 2f
}

// endregion
