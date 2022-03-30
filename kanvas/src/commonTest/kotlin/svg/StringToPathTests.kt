package com.juul.krayon.kanvas.svg

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
import kotlin.test.Test
import kotlin.test.assertEquals

class StringToPathTests {

    @Test
    fun lex_withEmptyString_returnsNoTokens() {
        assertEquals(0, lex("").count())
    }

    @Test
    fun lex_withDelimitersOnly_returnsNoTokens() {
        assertEquals(0, lex(" ,\n  ,,\n\n").count())
    }

    @Test
    fun lex_withCommandOnly_returnsCommandToken() {
        assertEquals(CommandToken(AbsoluteMoveTo), lex("M").single())
        assertEquals(CommandToken(AbsoluteClosePath), lex("Z").single())
        assertEquals(CommandToken(AbsoluteLineTo), lex("L").single())
        assertEquals(CommandToken(AbsoluteHorizontalLineTo), lex("H").single())
        assertEquals(CommandToken(AbsoluteVerticalLineTo), lex("V").single())
        assertEquals(CommandToken(AbsoluteCubicTo), lex("C").single())
        assertEquals(CommandToken(AbsoluteSmoothCubicTo), lex("S").single())
        assertEquals(CommandToken(AbsoluteQuadraticTo), lex("Q").single())
        assertEquals(CommandToken(AbsoluteSmoothQuadraticTo), lex("T").single())
        assertEquals(CommandToken(AbsoluteArc), lex("A").single())
        assertEquals(CommandToken(RelativeMoveTo), lex("m").single())
        assertEquals(CommandToken(RelativeClosePath), lex("z").single())
        assertEquals(CommandToken(RelativeLineTo), lex("l").single())
        assertEquals(CommandToken(RelativeHorizontalLineTo), lex("h").single())
        assertEquals(CommandToken(RelativeVerticalLineTo), lex("v").single())
        assertEquals(CommandToken(RelativeCubicTo), lex("c").single())
        assertEquals(CommandToken(RelativeSmoothCubicTo), lex("s").single())
        assertEquals(CommandToken(RelativeQuadraticTo), lex("q").single())
        assertEquals(CommandToken(RelativeSmoothQuadraticTo), lex("t").single())
        assertEquals(CommandToken(RelativeArc), lex("a").single())
    }

    @Test
    fun lex_withValueOnlyEndingString_returnsValue() {
        assertEquals(ValueToken(1.5f), lex("1.5").single())
    }

    @Test
    fun lex_withValueOnlyThenDelimiter_returnsValue() {
        assertEquals(ValueToken(1.5f), lex("1.5\n").single())
    }

    @Test
    fun lex_withSampleData_matchesSample() {
        val tokens = lex("m0,1l 23 -4.5 H-6e7z").toList()
        assertEquals(CommandToken(RelativeMoveTo), tokens[0])
        assertEquals(ValueToken(0f), tokens[1])
        assertEquals(ValueToken(1f), tokens[2])
        assertEquals(CommandToken(RelativeLineTo), tokens[3])
        assertEquals(ValueToken(23f), tokens[4])
        assertEquals(ValueToken(-4.5f), tokens[5])
        assertEquals(CommandToken(AbsoluteHorizontalLineTo), tokens[6])
        assertEquals(ValueToken(-6e7f), tokens[7])
        assertEquals(CommandToken(RelativeClosePath), tokens[8])
        assertEquals(9, tokens.size)
    }
}
