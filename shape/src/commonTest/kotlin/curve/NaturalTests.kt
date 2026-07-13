package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.svg.toPath
import com.juul.krayon.shape.Point
import com.juul.krayon.shape.area
import com.juul.krayon.shape.assertPathEquals
import com.juul.krayon.shape.line
import kotlin.test.Test

class NaturalTests {

    @Test
    fun line_curveNatural_generatesExpectedPath() {
        val l = line<Point>().curve(Natural)
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L1,3".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f))))
        assertPathEquals(
            "M0,1C0.333333,2,0.666667,3,1,3C1.333333,3,1.666667,2,2,1".toPath(),
            l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
        assertPathEquals(
            (
                "M0,1C0.333333,2.111111,0.666667,3.222222,1,3C1.333333,2.777778,1.666667,1.222222,2,1" +
                    "C2.333333,0.777778,2.666667,1.888889,3,3"
            ).toPath(),
            l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f), Point(3f, 3f))),
        )
    }

    @Test
    fun area_curveNatural_generatesExpectedPath() {
        val a = area<Point>().curve(Natural)
        assertPathEquals("M0,1L0,0Z".toPath(), a.render(listOf(Point(0f, 1f))))
        assertPathEquals(
            "M0,1C0.333333,2,0.666667,3,1,3C1.333333,3,1.666667,2,2,1L2,0C1.666667,0,1.333333,0,1,0C0.666667,0,0.333333,0,0,0Z".toPath(),
            a.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
    }
}
