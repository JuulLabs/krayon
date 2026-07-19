package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.svg.toPath
import com.juul.krayon.shape.Point
import com.juul.krayon.shape.area
import com.juul.krayon.shape.assertPathEquals
import com.juul.krayon.shape.line
import kotlin.test.Test

class CardinalTests {

    private val four = listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f), Point(3f, 3f))

    @Test
    fun line_curveCardinal_generatesExpectedPath() {
        val l = line<Point>().curve(Cardinal())
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L1,3".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f))))
        assertPathEquals(
            "M0,1C0,1,0.666667,3,1,3C1.333333,3,2,1,2,1".toPath(),
            l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
        assertPathEquals(
            "M0,1C0,1,0.666667,3,1,3C1.333333,3,1.666667,1,2,1C2.333333,1,3,3,3,3".toPath(),
            l.render(four),
        )
    }

    @Test
    fun line_curveCardinal_withTension_usesSpecifiedTension() {
        val l = line<Point>().curve(Cardinal(0.5f))
        assertPathEquals(
            "M0,1C0,1,0.833333,3,1,3C1.166667,3,1.833333,1,2,1C2.166667,1,3,3,3,3".toPath(),
            l.render(four),
        )
    }

    @Test
    fun area_curveCardinal_generatesExpectedPath() {
        val a = area<Point>().curve(Cardinal())
        assertPathEquals("M0,1L0,0Z".toPath(), a.render(listOf(Point(0f, 1f))))
        assertPathEquals(
            "M0,1C0,1,0.666667,3,1,3C1.333333,3,2,1,2,1L2,0C2,0,1.333333,0,1,0C0.666667,0,0,0,0,0Z".toPath(),
            a.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
    }

    @Test
    fun line_curveCardinalClosed_generatesExpectedPath() {
        val l = line<Point>().curve(CardinalClosed())
        assertPathEquals("M1,3L0,1Z".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f))))
        assertPathEquals(
            "M1,3C1.333333,3,1.666667,1,2,1C2.333333,1,3.333333,3,3,3C2.666667,3,0.333333,1,0,1C-0.333333,1,0.666667,3,1,3".toPath(),
            l.render(four),
        )
    }

    @Test
    fun line_curveCardinalOpen_generatesExpectedPath() {
        val l = line<Point>().curve(CardinalOpen())
        assertPathEquals("M1,3Z".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))))
        assertPathEquals(
            "M1,3C1.333333,3,1.666667,1,2,1".toPath(),
            l.render(four),
        )
    }
}
