package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.svg.toPath
import com.juul.krayon.shape.Point
import com.juul.krayon.shape.area
import com.juul.krayon.shape.assertPathEquals
import com.juul.krayon.shape.line
import kotlin.test.Test

class BasisTests {

    @Test
    fun line_curveBasis_generatesExpectedPath() {
        val l = line<Point>().curve(Basis)
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L1,3".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f))))
        assertPathEquals(
            (
                "M0,1L0.166667,1.333333C0.333333,1.666667,0.666667,2.333333,1,2.333333" +
                    "C1.333333,2.333333,1.666667,1.666667,1.833333,1.333333L2,1"
            ).toPath(),
            l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
    }

    @Test
    fun area_curveBasis_generatesExpectedPath() {
        val a = area<Point>().curve(Basis)
        assertPathEquals("M0,1L0,0Z".toPath(), a.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L1,3L1,0L0,0Z".toPath(), a.render(listOf(Point(0f, 1f), Point(1f, 3f))))
        assertPathEquals(
            (
                "M0,1L0.166667,1.333333C0.333333,1.666667,0.666667,2.333333,1,2.333333" +
                    "C1.333333,2.333333,1.666667,1.666667,1.833333,1.333333L2,1L2,0L1.833333,0" +
                    "C1.666667,0,1.333333,0,1,0C0.666667,0,0.333333,0,0.166667,0L0,0Z"
            ).toPath(),
            a.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
    }

    @Test
    fun line_curveBasisClosed_generatesExpectedPath() {
        val l = line<Point>().curve(BasisClosed)
        assertPathEquals("M0,0Z".toPath(), l.render(listOf(Point(0f, 0f))))
        assertPathEquals("M0,6.666667L0,3.333333Z".toPath(), l.render(listOf(Point(0f, 0f), Point(0f, 10f))))
        assertPathEquals(
            (
                "M1.666667,8.333333C3.333333,10,6.666667,10,6.666667,8.333333" +
                    "C6.666667,6.666667,3.333333,3.333333,1.666667,3.333333C0,3.333333,0,6.666667,1.666667,8.333333"
            ).toPath(),
            l.render(listOf(Point(0f, 0f), Point(0f, 10f), Point(10f, 10f))),
        )
        assertPathEquals(
            (
                "M1.666667,8.333333C3.333333,10,6.666667,10,8.333333,8.333333" +
                    "C10,6.666667,10,3.333333,8.333333,1.666667C6.666667,0,3.333333,0,1.666667,1.666667" +
                    "C0,3.333333,0,6.666667,1.666667,8.333333"
            ).toPath(),
            l.render(listOf(Point(0f, 0f), Point(0f, 10f), Point(10f, 10f), Point(10f, 0f))),
        )
    }

    @Test
    fun line_curveBasisOpen_generatesExpectedPath() {
        val l = line<Point>().curve(BasisOpen)
        assertPathEquals("M1.666667,8.333333Z".toPath(), l.render(listOf(Point(0f, 0f), Point(0f, 10f), Point(10f, 10f))))
        assertPathEquals(
            "M1.666667,8.333333C3.333333,10,6.666667,10,8.333333,8.333333".toPath(),
            l.render(listOf(Point(0f, 0f), Point(0f, 10f), Point(10f, 10f), Point(10f, 0f))),
        )
    }

    @Test
    fun area_curveBasisOpen_generatesExpectedPath() {
        val a = area<Point>().curve(BasisOpen)
        assertPathEquals("M1.666667,8.333333L1.666667,0Z".toPath(), a.render(listOf(Point(0f, 0f), Point(0f, 10f), Point(10f, 10f))))
        assertPathEquals(
            "M1.666667,8.333333C3.333333,10,6.666667,10,8.333333,8.333333L8.333333,0C6.666667,0,3.333333,0,1.666667,0Z".toPath(),
            a.render(listOf(Point(0f, 0f), Point(0f, 10f), Point(10f, 10f), Point(10f, 0f))),
        )
    }
}
