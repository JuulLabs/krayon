package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.svg.toPath
import com.juul.krayon.shape.Point
import com.juul.krayon.shape.area
import com.juul.krayon.shape.assertPathEquals
import com.juul.krayon.shape.line
import kotlin.test.Test

class StepTests {

    @Test
    fun line_curveStep_generatesExpectedPath() {
        val l = line<Point>().curve(Step)
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L1,1L1,3L2,3".toPath(), l.render(listOf(Point(0f, 1f), Point(2f, 3f))))
        assertPathEquals("M0,1L1,1L1,3L3,3L3,5L4,5".toPath(), l.render(listOf(Point(0f, 1f), Point(2f, 3f), Point(4f, 5f))))
    }

    @Test
    fun area_curveStep_generatesExpectedPath() {
        val a = area<Point>().curve(Step)
        assertPathEquals("M0,1L0,0Z".toPath(), a.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L1,1L1,3L2,3L2,0L1,0L1,0L0,0Z".toPath(), a.render(listOf(Point(0f, 1f), Point(2f, 3f))))
    }

    @Test
    fun line_curveStepBefore_generatesExpectedPath() {
        val l = line<Point>().curve(StepBefore)
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L0,3L2,3".toPath(), l.render(listOf(Point(0f, 1f), Point(2f, 3f))))
        assertPathEquals("M0,1L0,3L2,3L2,5L4,5".toPath(), l.render(listOf(Point(0f, 1f), Point(2f, 3f), Point(4f, 5f))))
    }

    @Test
    fun line_curveStepAfter_generatesExpectedPath() {
        val l = line<Point>().curve(StepAfter)
        assertPathEquals("M0,1Z".toPath(), l.render(listOf(Point(0f, 1f))))
        assertPathEquals("M0,1L2,1L2,3".toPath(), l.render(listOf(Point(0f, 1f), Point(2f, 3f))))
        assertPathEquals("M0,1L2,1L2,3L4,3L4,5".toPath(), l.render(listOf(Point(0f, 1f), Point(2f, 3f), Point(4f, 5f))))
    }
}
