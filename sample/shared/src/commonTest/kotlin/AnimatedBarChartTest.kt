package com.juul.krayon.sample

import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.sample.updaters.animatedBarChart
import com.juul.krayon.transition.hasPendingTransitions
import com.juul.krayon.transition.tickTransitions
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * End-to-end check of the animation pipeline: runs the real [animatedBarChart] updater against a
 * [RootElement] and drives the transition scheduler frame-by-frame (the same seam the platform render
 * loops use), asserting that bar heights actually animate from the baseline to their targets.
 */
class AnimatedBarChartTest {

    private val width = 400f
    private val height = 200f
    private val data = listOf(0.25f, 0.5f, 0.75f, 1.0f)

    private fun RootElement.bars(): List<RectangleElement> = queryAll(RectangleElement).toList()

    @Test
    fun barsAnimateFromBaselineToTargets() {
        val root = RootElement()

        // First frame: build the chart and start transitions from the baseline.
        root.tickTransitions(0)
        animatedBarChart(root, width, height, data)
        root.tickTransitions(0)

        val bars = root.bars()
        assertTrue(bars.size == data.size, "expected ${data.size} bars, got ${bars.size}")

        val innerHeight = height - 24f // matches the chart's margin of 12 on each side.
        val baselineTops = bars.map { it.top }
        // At the very start every bar sits at the baseline (zero height).
        baselineTops.forEach { top -> assertTrue(top == innerHeight, "bar should start at baseline") }
        assertTrue(root.hasPendingTransitions)

        // Midway through the first bar's animation (duration 750ms, no delay for index 0).
        root.tickTransitions(375)
        val midTop = root.bars()[0].top
        assertTrue(midTop < innerHeight, "bar should have grown above the baseline by the midpoint")
        assertTrue(midTop > 0f)

        // Well past the end of all staggered animations (last delay 90ms + 750ms duration).
        root.tickTransitions(2000)
        assertFalse(root.hasPendingTransitions, "animations should have completed")

        // Final heights should match the y-scale targets: taller bars for larger values (smaller top).
        val finalTops = root.bars().map { it.top }
        for (i in 1 until finalTops.size) {
            assertTrue(finalTops[i] < finalTops[i - 1], "larger values should produce taller bars")
        }
    }

    @Test
    fun newDataInterruptsAndResumesFromCurrentHeights() {
        val root = RootElement()
        root.tickTransitions(0)
        animatedBarChart(root, width, height, data)
        root.tickTransitions(0)
        root.tickTransitions(2000) // settle

        val settledTops = root.bars().map { it.top }

        // A new data set should re-animate the existing bars (no new elements created).
        root.tickTransitions(2000)
        animatedBarChart(root, width, height, listOf(1.0f, 0.75f, 0.5f, 0.25f))
        root.tickTransitions(2000)
        assertTrue(root.bars().size == data.size, "bar count should be stable across updates")
        assertTrue(root.hasPendingTransitions, "new data should schedule new transitions")

        root.tickTransitions(3000)
        val newTops = root.bars().map { it.top }
        assertTrue(newTops != settledTops, "bars should move towards the new targets")
    }
}
