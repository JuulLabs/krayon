package com.juul.krayon.kanvas.svg

import com.juul.krayon.kanvas.Segment
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
import kotlin.test.assertFails
import kotlin.test.assertTrue

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

    @Test
    fun parse_withNoTokens_returnsEmptyPath() {
        assertEquals(0, parse(lex("")).segments.size)
    }

    @Test
    fun parse_withNoCommand_fails() {
        assertFails { parse(lex("1")) }
    }

    @Test
    fun parse_withTooFewValues_fails() {
        assertFails { parse(lex("l 1")) }
    }

    @Test
    fun parse_withTooManyValues_fails() {
        assertFails { parse(lex("l 1 2 3")) }
    }

    @Test
    fun parse_withSingleValidSegment_returnsCorrectSegment() {
        assertEquals(Segment.MoveTo(1f, 2f), parse(lex("M1 2")).segments.single())
        assertEquals(Segment.Close, parse(lex("Z")).segments.single())
        assertEquals(Segment.LineTo(1f, 2f), parse(lex("L1 2")).segments.single())
        assertEquals(Segment.LineTo(1f, 0f), parse(lex("H1")).segments.single())
        assertEquals(Segment.LineTo(0f, 1f), parse(lex("V1")).segments.single())
        assertEquals(Segment.CubicTo(1f, 2f, 3f, 4f, 5f, 6f), parse(lex("C1 2 3 4 5 6")).segments.single())
        assertEquals(Segment.CubicTo(0f, 0f, 1f, 2f, 3f, 4f), parse(lex("S1 2 3 4")).segments.single())
        assertEquals(Segment.QuadraticTo(1f, 2f, 3f, 4f), parse(lex("Q1 2 3 4")).segments.single())
        assertEquals(Segment.QuadraticTo(0f, 0f, 1f, 2f), parse(lex("T1 2")).segments.single())
        // Because RelativePathBuilder is used as an implementation detail, these end up a looking a little different than they should.
        // Ideally, these would use RelativeXyz versions of all the segments and not need the "m50 50" as the first segment. As is these
        // don't really fit the spirit of what this test was meant to cover.
        assertEquals(Segment.MoveTo(51f, 52f), parse(lex("m50 50 m1 2")).segments.last())
        assertEquals(Segment.Close, parse(lex("m50 50 z")).segments.last())
        assertEquals(Segment.LineTo(51f, 52f), parse(lex("m50 50 l1 2")).segments.last())
        assertEquals(Segment.LineTo(51f, 50f), parse(lex("m50 50 h1")).segments.last())
        assertEquals(Segment.LineTo(50f, 51f), parse(lex("m50 50 v1")).segments.last())
        assertEquals(Segment.CubicTo(51f, 52f, 53f, 54f, 55f, 56f), parse(lex("m50 50 c1 2 3 4 5 6")).segments.last())
        assertEquals(Segment.CubicTo(50f, 50f, 51f, 52f, 53f, 54f), parse(lex("m50 50 s1 2 3 4")).segments.last())
        assertEquals(Segment.QuadraticTo(51f, 52f, 53f, 54f), parse(lex("m50 50 q1 2 3 4")).segments.last())
        assertEquals(Segment.QuadraticTo(50f, 50f, 51f, 52f), parse(lex("m50 50 t1 2")).segments.last())
        // Arcs are not represented as actual arc segments because our arc abstraction is weaker than SVG's (thanks, Android...). As such, we use
        // Android's arc-to-cubic translations. Here we just check that the segments are correctly typed.
        assertTrue(parse(lex("A 50 50 0 0 0 100 0")).segments.all { it is Segment.CubicTo })
        assertTrue(parse(lex("a 50 50 0 0 0 100 0")).segments.all { it is Segment.CubicTo })
    }

    @Test
    fun parse_withSmoothCurves_usesReflectedControlPoint() {
        val cubicSegments = parse(lex("S1 2 3 4 S5 6 7 8 s2 2 4 4")).segments
        assertEquals(Segment.CubicTo(0f, 0f, 1f, 2f, 3f, 4f), cubicSegments[0])
        assertEquals(Segment.CubicTo(5f, 6f, 5f, 6f, 7f, 8f), cubicSegments[1])
        assertEquals(Segment.CubicTo(9f, 10f, 9f, 10f, 11f, 12f), cubicSegments[2])
        assertEquals(3, cubicSegments.size)

        val quadraticSegments = parse(lex("T1 2 T3 4 t2 2")).segments
        assertEquals(Segment.QuadraticTo(0f, 0f, 1f, 2f), quadraticSegments[0])
        assertEquals(Segment.QuadraticTo(2f, 4f, 3f, 4f), quadraticSegments[1])
        assertEquals(Segment.QuadraticTo(4f, 4f, 5f, 6f), quadraticSegments[2])
        assertEquals(3, quadraticSegments.size)
    }

    // TODO: Figure out some sample cases for arc to verify that the produced cubic curves are reasonable/correct.
}
