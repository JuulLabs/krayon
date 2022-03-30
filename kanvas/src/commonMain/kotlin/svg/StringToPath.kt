package com.juul.krayon.kanvas.svg

import com.juul.krayon.kanvas.svg.Token.CommandToken

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
                yield(Token.ValueToken(value))
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
        yield(Token.ValueToken(value))
    }
}
