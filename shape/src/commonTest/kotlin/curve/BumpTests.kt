package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.svg.toPath
import com.juul.krayon.shape.Point
import com.juul.krayon.shape.area
import com.juul.krayon.shape.assertPathEquals
import com.juul.krayon.shape.line
import kotlin.test.Test

class BumpTests {

    @Test
    fun line_curveBumpX_generatesExpectedPath() {
        val l = line<Point>().curve(BumpX)
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1C0.5,1,0.5,3,1,3".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f))))
        assertPathEquals(
            "M0,1C0.5,1,0.5,3,1,3C1.5,3,1.5,1,2,1".toPath(),
            l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
    }

    @Test
    fun area_curveBumpX_generatesExpectedPath() {
        val a = area<Point>().curve(BumpX)
        assertPathEquals("M0,1L0,0Z".toPath(), a.render(listOf(Point(0f, 1f))))
        assertPathEquals(
            "M0,1C0.5,1,0.5,3,1,3L1,0C0.5,0,0.5,0,0,0Z".toPath(),
            a.render(listOf(Point(0f, 1f), Point(1f, 3f))),
        )
    }

    @Test
    fun line_curveBumpY_generatesExpectedPath() {
        val l = line<Point>().curve(BumpY)
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1C0,2,1,2,1,3".toPath(), l.render(listOf(Point(0f, 1f), Point(1f, 3f))))
        assertPathEquals(
            "M0,1C0,2,1,2,1,3C1,2,2,2,2,1".toPath(),
            l.render(listOf(Point(0f, 1f), Point(1f, 3f), Point(2f, 1f))),
        )
    }
}
