package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.svg.toPath
import com.juul.krayon.shape.Point
import com.juul.krayon.shape.area
import com.juul.krayon.shape.assertPathEquals
import com.juul.krayon.shape.line
import kotlin.test.Test

class CatmullRomTests {

    private val four = listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f), Point(3f, 3f))

    @Test
    fun line_curveCatmullRom_generatesExpectedPath() {
        val l = line<Point>().curve(CatmullRom())
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
    fun area_curveCatmullRom_alpha0_generatesExpectedPath() {
        val a = area<Point>().curve(CatmullRom(0f))
        assertPathEquals(
            "M0,1C0,1,0.666667,3,1,3C1.333333,3,2,1,2,1L2,0C2,0,1.333333,0,1,0C0.666667,0,0,0,0,0Z".toPath(),
            a.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
    }

    @Test
    fun line_curveCatmullRomClosed_generatesExpectedPath() {
        val l = line<Point>().curve(CatmullRomClosed())
        assertPathEquals("M1,3L0,1Z".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f))))
        assertPathEquals(
            "M1,3C1.333333,3,2.200267,1.324038,2,1C1.810600,0.693544,0.189400,0.693544,0,1C-0.200267,1.324038,0.666667,3,1,3".toPath(),
            l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
        assertPathEquals(
            (
                "M1,3C1.333333,3,1.666667,1,2,1C2.333333,1,3.160469,2.858341,3,3" +
                    "C2.796233,3.179882,0.203767,0.820118,0,1C-0.160469,1.141659,0.666667,3,1,3"
            ).toPath(),
            l.render(four),
        )
    }

    @Test
    fun line_curveCatmullRomOpen_generatesExpectedPath() {
        val l = line<Point>().curve(CatmullRomOpen())
        assertPathEquals("M1,3Z".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))))
        assertPathEquals(
            "M1,3C1.333333,3,1.666667,1,2,1".toPath(),
            l.render(four),
        )
    }
}
