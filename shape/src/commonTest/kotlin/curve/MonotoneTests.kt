package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.svg.toPath
import com.juul.krayon.shape.Point
import com.juul.krayon.shape.area
import com.juul.krayon.shape.assertPathEquals
import com.juul.krayon.shape.line
import kotlin.test.Test

class MonotoneTests {

    @Test
    fun line_curveMonotoneX_generatesExpectedPath() {
        val l = line<Point>().curve(MonotoneX)
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L1,3".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f))))
        assertPathEquals(
            "M0,1C0.333333,2,0.666667,3,1,3C1.333333,3,1.666667,2,2,1".toPath(),
            l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
        assertPathEquals(
            "M0,1C0.333333,2,0.666667,3,1,3C1.333333,3,1.666667,1,2,1C2.333333,1,2.666667,2,3,3".toPath(),
            l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f), Point(3f, 3f))),
        )
    }

    @Test
    fun line_curveMonotoneX_preservesMonotonicity() {
        val l = line<Point>().curve(MonotoneX)
        assertPathEquals(
            (
                "M0,200C33.333333,150,66.666667,100,100,100C133.333333,100,166.666667,100,200,100" +
                    "C233.333333,100,266.666667,300,300,300C333.333333,300,366.666667,300,400,300"
            ).toPath(),
            l.render(listOf(Point(0f, 200f), Point(100f, 100f), Point(200f, 100f), Point(300f, 300f), Point(400f, 300f))),
        )
    }

    @Test
    fun area_curveMonotoneX_generatesExpectedPath() {
        val a = area<Point>().curve(MonotoneX)
        assertPathEquals(
            "M0,1C0.333333,2,0.666667,3,1,3C1.333333,3,1.666667,2,2,1L2,0C1.666667,0,1.333333,0,1,0C0.666667,0,0.333333,0,0,0Z".toPath(),
            a.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
    }

    @Test
    fun line_curveMonotoneY_generatesExpectedPath() {
        val l = line<Point>().curve(MonotoneY)
        assertPathEquals("M1,0Z".toPath(), l.render(listOf(Point(1f, 0f))))
        assertPathEquals("M1,0L3,1".toPath(), l.render(listOf(Point(1f, 0f), Point(3f, 1f))))
        assertPathEquals(
            "M1,0C2,0.333333,3,0.666667,3,1C3,1.333333,2,1.666667,1,2".toPath(),
            l.render(listOf(Point(1f, 0f), Point(3f, 1f), Point(1f, 2f))),
        )
        assertPathEquals(
            "M1,0C2,0.333333,3,0.666667,3,1C3,1.333333,1,1.666667,1,2C1,2.333333,2,2.666667,3,3".toPath(),
            l.render(listOf(Point(1f, 0f), Point(3f, 1f), Point(1f, 2f), Point(3f, 3f))),
        )
    }
}
