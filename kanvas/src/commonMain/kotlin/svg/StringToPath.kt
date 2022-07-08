package com.juul.krayon.kanvas.svg

import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.RelativePathBuilder
import com.juul.krayon.kanvas.SegmentedPath
import com.juul.krayon.kanvas.SegmentedPathBuilder
import com.juul.krayon.kanvas.svg.Command.AbsoluteArc
import com.juul.krayon.kanvas.svg.Command.AbsoluteClosePath
import com.juul.krayon.kanvas.svg.Command.AbsoluteCubicTo
import com.juul.krayon.kanvas.svg.Command.AbsoluteHorizontalLineTo
import com.juul.krayon.kanvas.svg.Command.AbsoluteLineTo
import com.juul.krayon.kanvas.svg.Command.AbsoluteMoveTo
import com.juul.krayon.kanvas.svg.Command.AbsoluteQuadraticTo
import com.juul.krayon.kanvas.svg.Command.AbsoluteSmoothCubicTo
import com.juul.krayon.kanvas.svg.Command.AbsoluteSmoothQuadraticTo
import com.juul.krayon.kanvas.svg.Command.AbsoluteVerticalLineTo
import com.juul.krayon.kanvas.svg.Command.RelativeArc
import com.juul.krayon.kanvas.svg.Command.RelativeClosePath
import com.juul.krayon.kanvas.svg.Command.RelativeCubicTo
import com.juul.krayon.kanvas.svg.Command.RelativeHorizontalLineTo
import com.juul.krayon.kanvas.svg.Command.RelativeLineTo
import com.juul.krayon.kanvas.svg.Command.RelativeMoveTo
import com.juul.krayon.kanvas.svg.Command.RelativeQuadraticTo
import com.juul.krayon.kanvas.svg.Command.RelativeSmoothCubicTo
import com.juul.krayon.kanvas.svg.Command.RelativeSmoothQuadraticTo
import com.juul.krayon.kanvas.svg.Command.RelativeVerticalLineTo
import com.juul.krayon.kanvas.svg.Token.CommandToken
import com.juul.krayon.kanvas.svg.Token.ValueToken
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/** Parses `this` as [SVG Path data](https://www.w3.org/TR/SVG/paths.html#PathData) to a [Path], throwing an exception if the parsing fails. */
public fun String.toPath(): Path = Path(parse(lex(this)))

/** Parses `this` as [SVG Path data](https://www.w3.org/TR/SVG/paths.html#PathData) to a [Path], returning null if the parsing fails. */
public fun String.toPathOrNull(): Path? = try {
    toPath()
} catch (_: Exception) {
    null
}

// A -lot- of this class is left `internal` instead of `private` because `@VisibleForTesting` isn't available in Kotlin Multiplatform

internal enum class Command(
    val command: Char,
    val numArgs: Int,
) {
    AbsoluteMoveTo(command = 'M', numArgs = 2),
    AbsoluteClosePath(command = 'Z', numArgs = 0),
    AbsoluteLineTo(command = 'L', numArgs = 2),
    AbsoluteHorizontalLineTo(command = 'H', numArgs = 1),
    AbsoluteVerticalLineTo(command = 'V', numArgs = 1),
    AbsoluteCubicTo(command = 'C', numArgs = 6),
    AbsoluteSmoothCubicTo(command = 'S', numArgs = 4),
    AbsoluteQuadraticTo(command = 'Q', numArgs = 4),
    AbsoluteSmoothQuadraticTo(command = 'T', numArgs = 2),
    AbsoluteArc(command = 'A', numArgs = 7),
    RelativeMoveTo(command = 'm', numArgs = 2),
    RelativeClosePath(command = 'z', numArgs = 0),
    RelativeLineTo(command = 'l', numArgs = 2),
    RelativeHorizontalLineTo(command = 'h', numArgs = 1),
    RelativeVerticalLineTo(command = 'v', numArgs = 1),
    RelativeCubicTo(command = 'c', numArgs = 6),
    RelativeSmoothCubicTo(command = 's', numArgs = 4),
    RelativeQuadraticTo(command = 'q', numArgs = 4),
    RelativeSmoothQuadraticTo(command = 't', numArgs = 2),
    RelativeArc(command = 'a', numArgs = 7);
}

private val charToCommandCache = enumValues<Command>().associateBy { it.command }
private fun Char.toCommandOrNull(): Command? = charToCommandCache[this]

internal sealed class Token {
    data class CommandToken(val command: Command) : Token()
    data class ValueToken(val value: Float) : Token()
}

private fun Char.isDelimiter(): Boolean = this.isWhitespace() || this == ','

/** Lex the path string into a list of tokens. */
internal fun lex(string: String): Sequence<Token> = sequence {
    var tokenStart = 0
    var tokenStop = 0

    while (tokenStop < string.length) {
        val char = string[tokenStop]
        val isDelimiter = char.isDelimiter()
        val command = char.toCommandOrNull()

        if (isDelimiter || command != null) {
            if (tokenStop > tokenStart) {
                // Our read head just moved past the end of a value
                val value = string.substring(tokenStart, tokenStop).toFloat()
                yield(ValueToken(value))
            }

            if (command != null) {
                // Our read head found a command
                yield(CommandToken(command))
            }

            tokenStop += 1
            tokenStart = tokenStop
        } else {
            tokenStop += 1
        }
    }

    // Our source string ended while parsing a token
    if (tokenStop > tokenStart) {
        val value = string.substring(tokenStart).toFloat()
        yield(ValueToken(value))
    }
}

internal fun parse(tokens: Sequence<Token>): SegmentedPath {
    val builder = CommandPathBuilder()
    var command: Command? = null
    var valueRequired = false
    val values = mutableListOf<Float>()
    for (token in tokens) {
        if (token is CommandToken) {
            check(!valueRequired) { "Found command ${token.command}, but expected additional value for command ${command?.command}." }
            if (token.command.numArgs == 0) {
                builder.push(token.command, emptyList())
                command = null
            } else {
                command = token.command
                valueRequired = true
            }
        } else {
            checkNotNull(command) { "Found value token before first command token." }
            values += (token as ValueToken).value
            val consumeArguments = command.numArgs == values.size
            if (consumeArguments) {
                builder.push(command, values)
                values.clear()
            }
            valueRequired = !consumeArguments
        }
    }
    check(!valueRequired) { "Incorrect number of arguments for $command, expected ${command?.numArgs} but got ${values.size}." }
    return builder.build()
}

private class CommandPathBuilder : RelativePathBuilder<SegmentedPath>() {

    private val delegate = SegmentedPathBuilder()

    private var lastControlPoint: Pair<Float, Float>? = null

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
        delegate.moveTo(x, y)
        lastControlPoint = null
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
        lastControlPoint = null
        delegate.lineTo(x, y)
    }

    override fun arcTo(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, forceMoveTo: Boolean) {
        super.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
        lastControlPoint = null
        delegate.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        super.quadraticTo(controlX, controlY, endX, endY)
        lastControlPoint = controlX to controlY
        delegate.quadraticTo(controlX, controlY, endX, endY)
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        super.cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
        lastControlPoint = endControlX to endControlY
        delegate.cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun close() {
        super.close()
        lastControlPoint = null
        delegate.close()
    }

    override fun reset() {
        super.reset()
        lastControlPoint = null
        delegate.reset()
    }

    override fun build(): SegmentedPath = delegate.build()

    fun push(command: Command, args: List<Float>) {
        when (command) {
            AbsoluteArc -> pushArc(command, args)
            AbsoluteClosePath -> close()
            AbsoluteCubicTo -> cubicTo(args[0], args[1], args[2], args[3], args[4], args[5])
            AbsoluteHorizontalLineTo -> lineTo(args[0], state.lastY)
            AbsoluteLineTo -> lineTo(args[0], args[1])
            AbsoluteMoveTo -> moveTo(args[0], args[1])
            AbsoluteQuadraticTo -> quadraticTo(args[0], args[1], args[2], args[3])
            AbsoluteSmoothCubicTo -> pushSmooth(command, args)
            AbsoluteSmoothQuadraticTo -> pushSmooth(command, args)
            AbsoluteVerticalLineTo -> lineTo(state.lastX, args[0])
            RelativeArc -> pushArc(command, args)
            RelativeClosePath -> close()
            RelativeCubicTo -> relativeCubicTo(args[0], args[1], args[2], args[3], args[4], args[5])
            RelativeHorizontalLineTo -> relativeLineTo(args[0], 0f)
            RelativeLineTo -> relativeLineTo(args[0], args[1])
            RelativeMoveTo -> relativeMoveTo(args[0], args[1])
            RelativeQuadraticTo -> relativeQuadraticTo(args[0], args[1], args[2], args[3])
            RelativeSmoothCubicTo -> pushSmooth(command, args)
            RelativeSmoothQuadraticTo -> pushSmooth(command, args)
            RelativeVerticalLineTo -> relativeLineTo(0f, args[0])
        }
    }

    private fun pushSmooth(command: Command, args: List<Float>) {
        val state = this.state // copy prevents re-allocating every read
        val lastControlPoint = this.lastControlPoint // copy helps with type inference
        if (lastControlPoint == null) {
            when (command) {
                AbsoluteSmoothCubicTo -> cubicTo(state.lastX, state.lastY, args[0], args[1], args[2], args[3])
                AbsoluteSmoothQuadraticTo -> quadraticTo(state.lastX, state.lastY, args[0], args[1])
                RelativeSmoothCubicTo -> relativeCubicTo(0f, 0f, args[0], args[1], args[2], args[3])
                RelativeSmoothQuadraticTo -> relativeQuadraticTo(0f, 0f, args[0], args[1])
                else -> error("Illegal command for `pushSmooth`: $command")
            }
        } else {
            val dx = state.lastX - lastControlPoint.first
            val dy = state.lastY - lastControlPoint.second
            when (command) {
                AbsoluteSmoothCubicTo -> cubicTo(state.lastX + dx, state.lastY + dy, args[0], args[1], args[2], args[3])
                AbsoluteSmoothQuadraticTo -> quadraticTo(state.lastX + dx, state.lastY + dy, args[0], args[1])
                RelativeSmoothCubicTo -> relativeCubicTo(dx, dy, args[0], args[1], args[2], args[3])
                RelativeSmoothQuadraticTo -> relativeQuadraticTo(dx, dy, args[0], args[1])
                else -> error("Illegal command for `pushSmooth`: $command")
            }
        }
    }

    private fun pushArc(command: Command, args: List<Float>) {
        check(command == AbsoluteArc || command == RelativeArc) {
            "Command must be one of AbsoluteArc or RelativeArc, but was $command."
        }
        val state = this.state // copy prevents re-allocating every read
        val rx = abs(args[0])
        val ry = abs(args[1])
        val x = args[5]
        val y = args[6]
        if (rx <= 0f || ry <= 0f) {
            // when radius is zero, treat it as infinite (straight line)
            when (command) {
                AbsoluteArc -> lineTo(x, y)
                RelativeArc -> relativeLineTo(x, y)
                else -> error("Unreachable.")
            }
        } else {
            val xAxisRotation = args[2]
            val largeArcFlag = args[3] != 0f
            val sweepFlag = args[4] != 0f
            pushArc(
                x0 = state.lastX,
                y0 = state.lastY,
                x1 = x + if (command == RelativeArc) state.lastX else 0f,
                y1 = y + if (command == RelativeArc) state.lastY else 0f,
                a = rx,
                b = ry,
                thetaDegrees = xAxisRotation,
                isMoreThanHalf = largeArcFlag,
                isPositiveArc = sweepFlag,
            )
        }
    }

    /** Translated from Android's PathParser: https://android.googlesource.com/platform/frameworks/base/+/17e64ffd852f8fe23b8e2e2ff1b62ee742af17a6/core/java/android/util/PathParser.java#381 */
    private fun pushArc(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        a: Float,
        b: Float,
        thetaDegrees: Float,
        isMoreThanHalf: Boolean,
        isPositiveArc: Boolean,
    ) {
        val theta = thetaDegrees * PI.toFloat() / 180
        // Pre-compute rotation matrix entries
        val cosTheta = cos(theta)
        val sinTheta = sin(theta)
        // Transform (x0, y0) and (x1, y1) into unit space using (inverse) rotation, followed by (inverse) scale
        val x0p = (x0 * cosTheta + y0 * sinTheta) / a
        val y0p = (-x0 * sinTheta + y0 * cosTheta) / b
        val x1p = (x1 * cosTheta + y1 * sinTheta) / a
        val y1p = (-x1 * sinTheta + y1 * cosTheta) / b

        // Compute differences and averages
        val dx = x0p - x1p
        val dy = y0p - y1p
        val xm = (x0p + x1p) / 2
        val ym = (y0p + y1p) / 2

        // Solve for intersecting unit circles
        val dsq = dx * dx + dy * dy
        if (dsq == 0f) {
            return // Points are coincident
        }
        val disc = 1f / dsq - 1f / 4f
        if (disc < 0f) {
            // Target position is too far apart, so scale the ellipse while preserving the requested aspect ratio.
            val adjust = sqrt(dsq) / 1.99999f // I have no idea why the original source uses 1.99999 instead of 2
            pushArc(x0, y0, x1, y1, a * adjust, b * adjust, thetaDegrees, isMoreThanHalf, isPositiveArc)
            return // Don't finish calculation here because we recursed
        }
        val s = sqrt(disc)
        val sdx = s * dx
        val sdy = s * dy
        var (cx, cy) = when (isMoreThanHalf == isPositiveArc) {
            true -> (xm - sdy) to (ym + sdx)
            false -> (xm + sdy) to (ym - sdx)
        }

        val eta0 = atan2(y0p - cy, x0p - cx)
        val eta1 = atan2(y1p - cy, x1p - cx)

        val sweep = run {
            val sweep = eta1 - eta0
            when {
                isPositiveArc == (sweep >= 0) -> sweep
                sweep > 0 -> sweep - 2 * PI.toFloat()
                else -> sweep + 2 * PI.toFloat()
            }
        }

        cx *= a
        cy *= b
        val tcx = cx
        cx = cx * cosTheta - cy * sinTheta
        cy = tcx * sinTheta + cy * cosTheta

        arcToCubic(cx, cy, a, b, x0, y0, thetaDegrees, eta0, sweep)
    }

    /** Translated from Android's PathParser. https://android.googlesource.com/platform/frameworks/base/+/17e64ffd852f8fe23b8e2e2ff1b62ee742af17a6/core/java/android/util/PathParser.java#472 */
    private fun arcToCubic(
        cx: Float,
        cy: Float,
        a: Float,
        b: Float,
        e1x: Float,
        e1y: Float,
        theta: Float,
        start: Float,
        sweep: Float,
    ) {
        // Shadow some parameters because Java implementation was mutating them
        var e1x = e1x
        var e1y = e1y
        // Maximum of 45 degrees per cubic Bezier segment
        val numSegments = abs(ceil(sweep * 4 / PI).toInt())
        var eta1 = start
        val cosTheta = cos(theta)
        val sinTheta = sin(theta)
        val cosEta1 = cos(eta1)
        val sinEta1 = sin(eta1)
        var ep1x = -a * cosTheta * sinEta1 - b * sinTheta * cosEta1
        var ep1y = -a * sinTheta * sinEta1 + b * cosTheta * cosEta1
        val anglePerSegment = sweep / numSegments
        for (i in 0 until numSegments) {
            val eta2 = eta1 + anglePerSegment
            val sinEta2 = sin(eta2)
            val cosEta2 = cos(eta2)
            val e2x = cx + a * cosTheta * cosEta2 - b * sinTheta * sinEta2
            val e2y = cy + a * sinTheta * cosEta2 + b * cosTheta * sinEta2
            val ep2x = -a * cosTheta * sinEta2 - b * sinTheta * cosEta2
            val ep2y = -a * sinTheta * sinEta2 + b * cosTheta * cosEta2
            val tanDiff2 = tan((eta2 - eta1) / 2)
            val alpha = sin(eta2 - eta1) * (sqrt(4 + 3 * tanDiff2 * tanDiff2) - 1) / 3
            val q1x = e1x + alpha * ep1x
            val q1y = e1y + alpha * ep1y
            val q2x = e2x - alpha * ep2x
            val q2y = e2y - alpha * ep2y
            cubicTo(q1x, q1y, q2x, q2y, e2x, e2y)
            eta1 = eta2
            e1x = e2x
            e1y = e2y
            ep1x = ep2x
            ep1y = ep2y
        }
    }
}
