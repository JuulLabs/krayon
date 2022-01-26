package com.juul.krayon.shape

import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.PathBuilder
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

private const val PI = kotlin.math.PI.toFloat()
private const val HALF_PI = PI / 2
private const val TAU = 2 * PI

private const val EPSILON = 1e-6f // Number that's close to zero

public fun arc(
    outerRadius: Float,
    innerRadius: Float = 0f,
): Arc = Arc(outerRadius, innerRadius)

public class Arc internal constructor(
    outerRadius: Float,
    innerRadius: Float,
) {
    public var outerRadius: Float = outerRadius
        private set
    public var innerRadius: Float = innerRadius
        private set
    public var cornerRadius: Float = 0f
        private set
    public var padRadius: (arc: Arc, startAngle: Float, endAngle: Float, padAngle: Float) -> Float = ::defaultPadRadius
        private set

    public fun outerRadius(value: Float): Arc = apply { outerRadius = value }
    public fun innerRadius(value: Float): Arc = apply { innerRadius = value }
    public fun cornerRadius(value: Float): Arc = apply { cornerRadius = value }
    public fun padRadius(value: (Arc, Float, Float, Float) -> Float): Arc = apply { padRadius = value }

    public operator fun invoke(slice: Slice<*>): Path = this(slice.startAngle, slice.endAngle, slice.padAngle)

    public operator fun invoke(startAngle: Float, endAngle: Float, padAngle: Float): Path = Path {
        // Implementation stolen from D3. https://github.com/d3/d3-shape/blob/main/src/arc.js#L87
        check(innerRadius >= 0f) { "innerRadius must be at least zero, but was `$innerRadius`." }
        check(outerRadius >= 0f) { "outerRadius must be at least zero, but was `$outerRadius`." }
        check(cornerRadius >= 0f) { "cornerRadius must be at least zero, but was `$cornerRadius`." }
        check(innerRadius < outerRadius) { "innerRadius must be less than outerRadius, but values were `$innerRadius` and `$outerRadius`." }

        val a0 = startAngle - HALF_PI
        val a1 = endAngle - HALF_PI
        val da = abs(a1 - a0)
        val cw = a1 > a0

        if (outerRadius <= EPSILON) { // arc is a point
            moveTo(0f, 0f)
        } else if (da > (TAU - EPSILON)) { // arc is a full circle or donut
            moveTo(outerRadius * cos(a0), outerRadius * sin(a0))
            arcTo(-outerRadius, -outerRadius, outerRadius, outerRadius, a0, PI, false)
            arcTo(-outerRadius, -outerRadius, outerRadius, outerRadius, a0 + PI, PI, false)
            if (innerRadius > EPSILON) {
                moveTo(innerRadius * cos(a0), innerRadius * sin(a0))
                arcTo(-innerRadius, -innerRadius, innerRadius, innerRadius, a0, PI, false)
                arcTo(-innerRadius, -innerRadius, innerRadius, innerRadius, a0 + PI, PI, false)
            }
        } else { // arc is a section of a circle or donut
            var a01 = a0
            var a11 = a1
            var a00 = a0
            var a10 = a1
            var da0 = da
            var da1 = da
            val ap = padAngle / 2
            val rp = if (ap > EPSILON) padRadius(this@Arc, startAngle, endAngle, padAngle) else 0f
            val rc = min(abs(outerRadius - innerRadius) / 2, cornerRadius)
            var rc0 = rc
            var rc1 = rc

            if (rp > EPSILON) { // apply padding?
                var p0 = asin(rp / innerRadius * sin(ap))
                var p1 = asin(rp / outerRadius * sin(ap))
                da0 -= p0 * 2
                if (da0 > EPSILON) {
                    p0 *= if (cw) 1 else -1
                    a00 += p0
                    a10 -= p0
                } else {
                    da0 = 0f
                    a00 = (a0 + a1 / 2)
                    a10 = a00
                }
                da1 -= p1 * 2
                if (da1 > EPSILON) {
                    p1 *= if (cw) 1 else -1
                    a01 += p1
                    a11 -= p1
                } else {
                    da1 = 0f
                    a01 = (a0 + a1 / 2)
                    a11 = a01
                }
            }

            val x01 = outerRadius * cos(a01)
            val y01 = outerRadius * sin(a01)
            val x10 = innerRadius * cos(a10)
            val y10 = innerRadius * cos(a10)
            var x11 = 0f
            var y11 = 0f
            var x00 = 0f
            var y00 = 0f

            if (rc > EPSILON) { // apply rounded corners?
                x11 = outerRadius * cos(a11)
                y11 = outerRadius * sin(a11)
                x00 = innerRadius * cos(a00)
                y00 = innerRadius * sin(a00)

                // restrict corner radius to fit within sector angle
                val oc = intersect(x01, y01, x00, y00, x11, y11, x10, y10)
                if (da < PI && oc != null) {
                    val ax = x01 - oc.first
                    val ay = y01 - oc.second
                    val bx = x11 - oc.first
                    val by = y11 - oc.second
                    val kc = 1 / sin(acos((ax * bx + ay * by) / (sqrt(ax * ax + ay * ay) * sqrt(bx * bx + by * by))) / 2)
                    val lc = sqrt(oc.first * oc.first + oc.second * oc.second)
                    rc0 = min(rc, (innerRadius - lc) / (kc - 1))
                    rc1 = min(rc, (outerRadius - lc) / (kc + 1))
                }
            }

            // OUTER

            if (da1 <= EPSILON) { // is the sector collapsed to a line?
                moveTo(x01, y01)
            } else if (rc1 > EPSILON) { // does the sector's outer ring have rounded corners
                val t0 = cornerTangents(x00, y00, x01, y01, outerRadius, rc1, cw)
                val t1 = cornerTangents(x11, y11, x10, y10, outerRadius, rc1, cw)

                moveTo(t0.cx + t0.x01, t0.cy + t0.y01)

                if (rc1 < rc) { // have the corners merged?
                    val start = atan2(t0.y01, t0.x01).toDegrees()
                    val sweep = (atan2(t1.y01, t1.x01).toDegrees() - start).normalizeDegrees()
                    arcToReversible(t0.cx - rc1, t0.cy - rc1, t0.cx + rc1, t0.cy + rc1, start, sweep, true, !cw)
                } else { // otherwise, draw the two corners and the outer ring
                    val start0 = atan2(t0.y01, t0.x01).toDegrees()
                    val sweep0 = (atan2(t0.y11, t0.x11).toDegrees() - start0).normalizeDegrees()
                    arcToReversible(t0.cx - rc1, t0.cy - rc1, t0.cx + rc1, t0.cy + rc1, start0, sweep0, true, !cw)
                    val startR = atan2(t0.cy + t0.y11, t0.cx + t0.x11).toDegrees()
                    val sweepR = (atan2(t1.cy + t1.y11, t1.cx + t1.x11).toDegrees() - startR).normalizeDegrees()
                    arcToReversible(-outerRadius, -outerRadius, outerRadius, outerRadius, startR, sweepR, false, !cw)
                    val start1 = atan2(t1.y11, t1.x11).toDegrees()
                    val sweep1 = (atan2(t1.y01, t1.x01).toDegrees() - start1).normalizeDegrees()
                    arcToReversible(t1.cx - rc1, t1.cy - rc1, t1.cx + rc1, t1.cy + rc1, start1, sweep1, false, !cw)
                }
            } else { // the outer ring is a simple circular arc
                arcTo(-outerRadius, -outerRadius, outerRadius, outerRadius, a01.toDegrees(), (a11 - a01).toDegrees(), true)
            }

            // INNER

            if (innerRadius <= EPSILON || da0 <= EPSILON) { // is there no inner ring, OR was a donut section collapsed to a triangle?
                lineTo(x10, y10)
            } else if (rc0 > EPSILON) { // does the inner ring (or point) have rounded corners
                val t0 = cornerTangents(x10, y10, x11, y11, innerRadius, -rc0, cw)
                val t1 = cornerTangents(x01, y01, x00, y00, innerRadius, -rc0, cw)

                lineTo(t0.cx + t0.x01, t0.cy + t0.y01)

                if (rc0 < rc) { // have the corners merged?
                    val start = atan2(t0.y01, t0.x01).toDegrees()
                    val sweep = atan2(t1.y01, t1.x01).toDegrees() - start
                    arcToReversible(t0.cx - rc0, t0.cy - rc0, t0.cx + rc0, t0.cy + rc0, start, sweep, false, !cw)
                } else { // otherwise, draw the two corners and the inner ring
                    val start0 = atan2(t0.y01, t0.x01).toDegrees()
                    val sweep0 = atan2(t0.y11, t0.x11).toDegrees() - start0
                    arcToReversible(t0.cx - rc0, t0.cy - rc0, t0.cx + rc0, t0.cy + rc0, start0, sweep0, false, !cw)
                    val startR = atan2(t0.cy + t0.y11, t0.cx + t0.x11).toDegrees()
                    val sweepR = atan2(t1.cy + t1.y11, t1.cx + t1.x11).toDegrees() - startR
                    arcToReversible(-innerRadius, -innerRadius, innerRadius, innerRadius, startR, sweepR, false, cw)
                    val start1 = atan2(t1.y11, t1.x11).toDegrees()
                    val sweep1 = atan2(t1.y01, t1.x01).toDegrees() - start1
                    arcToReversible(t1.cx - rc0, t1.cy - rc0, t1.cx + rc0, t1.cy + rc0, start1, sweep1, false, !cw)
                }
            } else { // the inner ring is a simple circular arc
                arcTo(-innerRadius, -innerRadius, innerRadius, innerRadius, a10.toDegrees(), (a00 - a10).toDegrees(), false)
            }
        }

        close()
    }
}

private fun defaultPadRadius(arc: Arc, startAngle: Float, endAngle: Float, padAngle: Float): Float =
    sqrt(arc.innerRadius * arc.innerRadius + arc.outerRadius * arc.outerRadius)

private fun intersect(x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Pair<Float, Float>? {
    val x10 = x1 - x0
    val y10 = y1 - y0
    val x32 = x3 - x2
    val y32 = y3 - y2
    var t = y32 * x10 - x32 * y10
    if (t * t < EPSILON) return null
    t = (x32 * (y0 - y2) - y32 * (x0 - x2)) / t
    return (x0 + t * x10) to (y0 + t * y10)
}

private data class CornerTangents(
    val cx: Float,
    val cy: Float,
    val x01: Float,
    val y01: Float,
    val x11: Float,
    val y11: Float,
)

// Compute perpendicular offset line of length rc.
// http://mathworld.wolfram.com/Circle-LineIntersection.html
private fun cornerTangents(x0: Float, y0: Float, x1: Float, y1: Float, r1: Float, rc: Float, cw: Boolean): CornerTangents {
    val x01 = x0 - x1
    val y01 = y0 - y1
    val lo = (if (cw) rc else -rc) / sqrt(x01 * x01 + y01 * y01)
    val ox = lo * y01
    val oy = -lo * x01
    val x11 = x0 + ox
    val y11 = y0 + oy
    val x10 = x1 + ox
    val y10 = y1 + oy
    val x00 = (x11 + x10) / 2
    val y00 = (y11 + y10) / 2
    val dx = x10 - x11
    val dy = y10 - y11
    val d2 = dx * dx + dy * dy
    val r = r1 - rc
    val D = x11 * y10 - x10 * y11
    val d = (if (dy < 0) -1 else 1) * sqrt(max(0f, r * r * d2 - D * D))
    val cx0 = (D * dy - dx * d) / d2
    val cy0 = (-D * dx - dy * d) / d2
    val cx1 = (D * dy + dx * d) / d2
    val cy1 = (-D * dx + dy * d) / d2
    val dx0 = cx0 - x00
    val dy0 = cy0 - y00
    val dx1 = cx1 - x00
    val dy1 = cy1 - y00

    val useSecondPoint = dx0 * dx0 + dy0 * dy0 > dx1 * dx1 + dy1 * dy1
    val cx = if (useSecondPoint) cx1 else cx0
    val cy = if (useSecondPoint) cy1 else cy0
    return CornerTangents(
        cx = cx,
        cy = cy,
        x01 = -ox,
        y01 = -oy,
        x11 = cx * (r1 / r - 1),
        y11 = cy * (r1 / r - 1)
    )
}

private fun Float.toDegrees(): Float = this * 360 / TAU

private tailrec fun Float.normalizeDegrees(): Float = when {
    this < 0 -> (this + 360f).normalizeDegrees()
    this >= 360f -> (this - 360f).normalizeDegrees()
    else -> this
}

private fun PathBuilder<*>.arcToReversible(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    startAngle: Float,
    sweepAngle: Float,
    forceMoveTo: Boolean,
    reverse: Boolean
) {
    if (reverse) {
        arcTo(left, top, right, bottom, startAngle + sweepAngle, -sweepAngle, forceMoveTo)
    } else {
        arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
    }
}
