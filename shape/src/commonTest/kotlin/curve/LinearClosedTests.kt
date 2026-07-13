package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.svg.toPath
import com.juul.krayon.shape.Point
import com.juul.krayon.shape.assertPathEquals
import com.juul.krayon.shape.line
import kotlin.test.Test

class LinearClosedTests {

    @Test
    fun line_curveLinearClosed_generatesExpectedPath() {
        val l = line<Point>().curve(LinearClosed)
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L2,3Z".toPath(), l.render(listOf(Point(0f, 1f), Point(2f, 3f))))
        assertPathEquals("M0,1L2,3L4,5Z".toPath(), l.render(listOf(Point(0f, 1f), Point(2f, 3f), Point(4f, 5f))))
    }
}
