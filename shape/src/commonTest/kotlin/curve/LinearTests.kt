package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.svg.toPath
import com.juul.krayon.shape.Point
import com.juul.krayon.shape.area
import com.juul.krayon.shape.line
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearTests {

    // D3: "line.curve(curveLinear)(data) generates the expected path"
    @Test
    fun line_curve_curveLinear_generatesExpectedPath() {
        val l = line<Point>().curve(Linear)
        assertEquals(
            expected = "M0,1Z".toPath(),
            actual = l.render(listOf(Point(0f, 1f))),
        )
        assertEquals(
            expected = "M0,1L2,3".toPath(),
            actual = l.render(listOf(Point(0f, 1f), Point(2f, 3f))),
        )
        assertEquals(
            expected = "M0,1L2,3L4,5".toPath(),
            actual = l.render(listOf(Point(0f, 1f), Point(2f, 3f), Point(4f, 5f))),
        )
    }

    // D3: "area.curve(curveLinear)(data) generates the expected path"
    @Test
    fun area_curve_curveLinear_generatesExpectedPath() {
        val a = area<Point>().curve(Linear)
        assertEquals(
            expected = "M0,1L0,0Z".toPath(),
            actual = a.render(listOf(Point(0f, 1f))),
        )
        assertEquals(
            expected = "M0,1L2,3L2,0L0,0Z".toPath(),
            actual = a.render(listOf(Point(0f, 1f), Point(2f, 3f))),
        )
        assertEquals(
            expected = "M0,1L2,3L4,5L4,0L2,0L0,0Z".toPath(),
            actual = a.render(listOf(Point(0f, 1f), Point(2f, 3f), Point(4f, 5f))),
        )
    }
}
