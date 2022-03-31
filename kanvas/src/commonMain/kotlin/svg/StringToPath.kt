package com.juul.krayon.kanvas.svg

import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.ReifiedPath
import com.juul.krayon.kanvas.ReifiedPathPathBuilder
import com.juul.krayon.kanvas.RelativePathBuilder
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
import kotlin.math.abs

/** Parses `this` as [SVG Path data](https://www.w3.org/TR/SVG/paths.html#PathData) to a [Path], throwing an exception if the parsing fails. */
public fun String.toPath(): Path = parse(lex(this))

/** Parses `this` as [SVG Path data](https://www.w3.org/TR/SVG/paths.html#PathData) to a [Path], returning null if the parsing fails. */
public fun String.toPathOrNull(): Path? = try {
    toPath()
} catch (_: Exception) {
    null
}

// A -lot- of this class is left `internal` instead of `private` because `@VisibleForTesting` isn't available in Kotlin Multiplatform

internal enum class Command(
    val command: Char,
    val numArgs: Int
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

internal fun parse(tokens: Sequence<Token>): ReifiedPath {
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

private class CommandPathBuilder : RelativePathBuilder<ReifiedPath>() {

    private val delegate = ReifiedPathPathBuilder()

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

    override fun build(): ReifiedPath = delegate.build()

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
        val state = this.state // copy prevents re-allocating every read
        val x = args[5]
        val y = args[6]
        if (x == state.lastX && y == state.lastY) {
            return // No-op
        }
        val rx = abs(args[0])
        val ry = abs(args[1])
        val xAxisRotation = args[2] // TODO: Warn or throw when this is non-zero, because it isn't multiplatform safe
        val largeArcFlag = args[3] != 0f // TODO: Check what other implementations do. Spec only mentions 0 and 1, not other values.
        val sweepFlag = args[4] != 0f // TODO: Check what other implementations do. Spec only mentions 0 and 1, not other values.
        if (rx == 0f || ry == 0f) when (command) {
            AbsoluteArc -> lineTo(x, y)
            RelativeArc -> relativeLineTo(x, y)
        } else {
            val left = args[5] - args[0]
            val top = args[6] - args[1]
            val right = args[5] + args[0]
            val bottom = args[6] + args[1]
            TODO("Continue from here. https://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes")
        }
    }
}
