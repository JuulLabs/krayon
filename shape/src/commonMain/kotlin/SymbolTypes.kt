package com.juul.krayon.shape

import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.PathBuilder
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

private const val PI: Float = kotlin.math.PI.toFloat()
private const val TAU: Float = 2f * PI

/** A symbol shape that can draw itself into a [PathBuilder] scaled to a given area [size] (in square pixels). */
public interface SymbolType {
    public fun draw(context: PathBuilder<*>, size: Float)
}

/** Draws this symbol at the given [size] and returns the resulting [Path]. */
public fun SymbolType.render(size: Float = 64f): Path = Path { draw(this, size) }

/** A circle, sized so that its area matches [size]. */
public object Circle : SymbolType {
    override fun draw(context: PathBuilder<*>, size: Float) {
        val r = sqrt(size / PI)
        context.moveTo(r, 0f)
        context.arcTo(-r, -r, r, r, 0f, 180f, false)
        context.arcTo(-r, -r, r, r, 180f, 180f, false)
    }
}

/** A Greek cross (plus sign) with arms of equal width. */
public object Cross : SymbolType {
    override fun draw(context: PathBuilder<*>, size: Float) {
        val r = sqrt(size / 5f) / 2f
        context.moveTo(-3 * r, -r)
        context.lineTo(-r, -r)
        context.lineTo(-r, -3 * r)
        context.lineTo(r, -3 * r)
        context.lineTo(r, -r)
        context.lineTo(3 * r, -r)
        context.lineTo(3 * r, r)
        context.lineTo(r, r)
        context.lineTo(r, 3 * r)
        context.lineTo(-r, 3 * r)
        context.lineTo(-r, r)
        context.lineTo(-3 * r, r)
        context.close()
    }
}

/** A rhombus. */
public object Diamond : SymbolType {
    private val tan30: Float = sqrt(1f / 3f)
    private val tan30x2: Float = tan30 * 2f

    override fun draw(context: PathBuilder<*>, size: Float) {
        val y = sqrt(size / tan30x2)
        val x = y * tan30
        context.moveTo(0f, -y)
        context.lineTo(x, 0f)
        context.lineTo(0f, y)
        context.lineTo(-x, 0f)
        context.close()
    }
}

/** An axis-aligned square. */
public object Square : SymbolType {
    override fun draw(context: PathBuilder<*>, size: Float) {
        val w = sqrt(size)
        val x = -w / 2f
        context.moveTo(x, x)
        context.lineTo(x + w, x)
        context.lineTo(x + w, x + w)
        context.lineTo(x, x + w)
        context.close()
    }
}

/** A five-pointed star. */
public object Star : SymbolType {
    private const val KA: Float = 0.89081309152928522810f
    private val kr: Float = sin(PI / 10f) / sin(7f * PI / 10f)
    private val kx: Float = sin(TAU / 10f) * kr
    private val ky: Float = -cos(TAU / 10f) * kr

    override fun draw(context: PathBuilder<*>, size: Float) {
        val r = sqrt(size * KA)
        val x = kx * r
        val y = ky * r
        context.moveTo(0f, -r)
        context.lineTo(x, y)
        for (i in 1 until 5) {
            val a = TAU * i / 5f
            val c = cos(a)
            val s = sin(a)
            context.lineTo(s * r, -c * r)
            context.lineTo(c * x - s * y, s * x + c * y)
        }
        context.close()
    }
}

/** An upward-pointing equilateral triangle. */
public object Triangle : SymbolType {
    private val sqrt3: Float = sqrt(3f)

    override fun draw(context: PathBuilder<*>, size: Float) {
        val y = -sqrt(size / (sqrt3 * 3f))
        context.moveTo(0f, y * 2f)
        context.lineTo(-sqrt3 * y, -y)
        context.lineTo(sqrt3 * y, -y)
        context.close()
    }
}

/** A "Y" shape. */
public object Wye : SymbolType {
    private const val C: Float = -0.5f
    private val s: Float = sqrt(3f) / 2f
    private val k: Float = 1f / sqrt(12f)
    private val a: Float = (k / 2f + 1f) * 3f

    override fun draw(context: PathBuilder<*>, size: Float) {
        val r = sqrt(size / a)
        val x0 = r / 2f
        val y0 = r * k
        val x1 = x0
        val y1 = r * k + r
        val x2 = -x1
        val y2 = y1
        context.moveTo(x0, y0)
        context.lineTo(x1, y1)
        context.lineTo(x2, y2)
        context.lineTo(C * x0 - s * y0, s * x0 + C * y0)
        context.lineTo(C * x1 - s * y1, s * x1 + C * y1)
        context.lineTo(C * x2 - s * y2, s * x2 + C * y2)
        context.lineTo(C * x0 + s * y0, C * y0 - s * x0)
        context.lineTo(C * x1 + s * y1, C * y1 - s * x1)
        context.lineTo(C * x2 + s * y2, C * y2 - s * x2)
        context.close()
    }
}

/** A plus sign drawn as two crossing strokes (intended to be stroked). */
public object Plus : SymbolType {
    override fun draw(context: PathBuilder<*>, size: Float) {
        val r = sqrt(size - min(size / 7f, 2f)) * 0.87559f
        context.moveTo(-r, 0f)
        context.lineTo(r, 0f)
        context.moveTo(0f, r)
        context.lineTo(0f, -r)
    }
}

/** An "X" drawn as two crossing strokes (intended to be stroked). */
public object Times : SymbolType {
    override fun draw(context: PathBuilder<*>, size: Float) {
        val r = sqrt(size - min(size / 6f, 1.7f)) * 0.6189f
        context.moveTo(-r, -r)
        context.lineTo(r, r)
        context.moveTo(-r, r)
        context.lineTo(r, -r)
    }
}

/** A six-armed asterisk drawn as three crossing strokes (intended to be stroked). */
public object Asterisk : SymbolType {
    private val sqrt3: Float = sqrt(3f)

    override fun draw(context: PathBuilder<*>, size: Float) {
        val r = sqrt(size + min(size / 28f, 0.75f)) * 0.59436f
        val t = r / 2f
        val u = t * sqrt3
        context.moveTo(0f, r)
        context.lineTo(0f, -r)
        context.moveTo(-u, -t)
        context.lineTo(u, t)
        context.moveTo(-u, t)
        context.lineTo(u, -t)
    }
}

/** Symbol types designed to be filled, matching `d3.symbolsFill`. */
public val symbolsFill: List<SymbolType> = listOf(Circle, Cross, Diamond, Square, Star, Triangle, Wye)

/** Symbol types designed to be stroked, matching the in-scope subset of `d3.symbolsStroke`. */
public val symbolsStroke: List<SymbolType> = listOf(Circle, Plus, Times, Asterisk)
