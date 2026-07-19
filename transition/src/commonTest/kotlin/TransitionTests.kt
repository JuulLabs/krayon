package com.juul.krayon.transition

import com.juul.krayon.color.Color
import com.juul.krayon.color.black
import com.juul.krayon.color.white
import com.juul.krayon.element.RootElement
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TransitionTests {

    private fun setup(count: Int): Pair<RootElement, Selection<AnimatableElement, Int>> {
        val root = RootElement()
        val selection = root.asSelection()
            .selectAll(AnimatableElement)
            .data((0 until count).toList())
            .join(AnimatableElement)
        return root to selection
    }

    private fun RootElement.animatables(): List<AnimatableElement> =
        children.filterIsInstance<AnimatableElement>()

    @Test
    fun floatAttr_interpolatesLinearly() {
        val (root, selection) = setup(1)
        selection.each { floatValue = 0f }
        selection.transition().duration(250).ease(easeLinear).attribute(AnimatableElement::floatValue, 100f)
        val element = root.animatables().single()

        assertTrue(root.tickTransitions(0))
        assertEquals(0f, element.floatValue, 1e-4f)
        root.tickTransitions(125)
        assertEquals(50f, element.floatValue, 1e-4f)
        val stillPending = root.tickTransitions(250)
        assertEquals(100f, element.floatValue, 1e-4f)
        assertFalse(stillPending)
        assertFalse(root.hasPendingTransitions)
    }

    @Test
    fun easing_isAppliedToFraction() {
        val (root, selection) = setup(1)
        selection.each { floatValue = 0f }
        // easeQuadraticIn(0.5) == 0.25, so value should be 25 at the midpoint of time.
        selection.transition().duration(100).ease(easeQuadraticIn).attribute(AnimatableElement::floatValue, 100f)
        val element = root.animatables().single()
        root.tickTransitions(0)
        root.tickTransitions(50)
        assertEquals(25f, element.floatValue, 1e-3f)
    }

    @Test
    fun delay_shiftsStart() {
        val (root, selection) = setup(1)
        selection.each { floatValue = 0f }
        selection.transition().delay(100).duration(100).ease(easeLinear).attribute(AnimatableElement::floatValue, 100f)
        val element = root.animatables().single()

        root.tickTransitions(0)
        assertEquals(0f, element.floatValue, 1e-4f)
        root.tickTransitions(50)
        assertEquals(0f, element.floatValue, 1e-4f, "should not move before delay elapses")
        root.tickTransitions(150)
        assertEquals(50f, element.floatValue, 1e-4f)
        root.tickTransitions(200)
        assertEquals(100f, element.floatValue, 1e-4f)
    }

    @Test
    fun durationZero_snapsToTarget() {
        val (root, selection) = setup(1)
        selection.each { floatValue = 10f }
        selection.transition().duration(0).attribute(AnimatableElement::floatValue, 99f)
        val element = root.animatables().single()
        root.tickTransitions(0)
        assertEquals(99f, element.floatValue, 1e-4f)
        assertFalse(root.hasPendingTransitions)
    }

    @Test
    fun intAndDoubleAndColorAttrs_interpolate() {
        val (root, selection) = setup(1)
        selection.each {
            intValue = 0
            doubleValue = 0.0
            color = black
        }
        selection.transition().duration(100).ease(easeLinear)
            .attribute(AnimatableElement::intValue, 100)
            .attribute(AnimatableElement::doubleValue, 10.0)
            .attribute(AnimatableElement::color, white)
        val element = root.animatables().single()

        root.tickTransitions(0)
        root.tickTransitions(50)
        assertEquals(50, element.intValue)
        assertEquals(5.0, element.doubleValue, 1e-6)
        // Halfway from black (0,0,0) to white (255,255,255) rounds to 128 per channel.
        assertEquals(128, element.color.red)
        assertEquals(128, element.color.green)
        assertEquals(128, element.color.blue)

        root.tickTransitions(100)
        assertEquals(100, element.intValue)
        assertEquals(10.0, element.doubleValue, 1e-6)
        assertEquals(Color(255, 255, 255), element.color)
    }

    @Test
    fun perElementDelay_stagger() {
        val (root, selection) = setup(3)
        selection.each { floatValue = 0f }
        selection.transition().duration(100).ease(easeLinear)
            .delay { (_, index) -> index * 100L }
            .attribute(AnimatableElement::floatValue, 100f)
        val elements = root.animatables()

        root.tickTransitions(0)
        root.tickTransitions(50)
        assertEquals(50f, elements[0].floatValue, 1e-4f)
        assertEquals(0f, elements[1].floatValue, 1e-4f)
        assertEquals(0f, elements[2].floatValue, 1e-4f)
        root.tickTransitions(150)
        assertEquals(100f, elements[0].floatValue, 1e-4f)
        assertEquals(50f, elements[1].floatValue, 1e-4f)
        assertEquals(0f, elements[2].floatValue, 1e-4f)
    }

    @Test
    fun onStartAndEnd_fireOnce() {
        val (root, selection) = setup(1)
        var starts = 0
        var ends = 0
        selection.transition().duration(100)
            .on(TransitionEvent.Start) { starts++ }
            .on(TransitionEvent.End) { ends++ }
            .attribute(AnimatableElement::floatValue, 1f)

        root.tickTransitions(0)
        assertEquals(1, starts)
        assertEquals(0, ends)
        root.tickTransitions(50)
        assertEquals(1, starts)
        assertEquals(0, ends)
        root.tickTransitions(100)
        assertEquals(1, starts)
        assertEquals(1, ends)
    }

    @Test
    fun remove_detachesElementOnEnd() {
        val (root, selection) = setup(1)
        selection.transition().duration(100).attribute(AnimatableElement::floatValue, 1f).remove()
        val element = root.animatables().single()

        root.tickTransitions(0)
        assertSame(root, element.parent)
        root.tickTransitions(100)
        assertNull(element.parent)
        assertTrue(root.animatables().isEmpty())
    }

    @Test
    fun chainedTransition_runsSequentially() {
        val (root, selection) = setup(1)
        selection.each { floatValue = 0f }
        val first = selection.transition().duration(100).ease(easeLinear).attribute(AnimatableElement::floatValue, 100f)
        first.transition().duration(100).ease(easeLinear).attribute(AnimatableElement::floatValue, 0f)
        val element = root.animatables().single()

        root.tickTransitions(0)
        root.tickTransitions(100) // first ends at 100
        assertEquals(100f, element.floatValue, 1e-4f)
        root.tickTransitions(150) // second half-way
        assertEquals(50f, element.floatValue, 1e-4f)
        root.tickTransitions(200)
        assertEquals(0f, element.floatValue, 1e-4f)
        assertFalse(root.hasPendingTransitions)
    }

    @Test
    fun recreatingTransition_interruptsAndResumesFromCurrentValue() {
        val (root, selection) = setup(1)
        selection.each { floatValue = 0f }
        var interrupts = 0
        selection.transition().duration(100).ease(easeLinear)
            .on(TransitionEvent.Interrupt) { interrupts++ }
            .attribute(AnimatableElement::floatValue, 100f)
        val element = root.animatables().single()

        root.tickTransitions(0)
        root.tickTransitions(50)
        assertEquals(50f, element.floatValue, 1e-4f)

        // A new transition of the same (default) name interrupts the running one.
        selection.transition().duration(100).ease(easeLinear).attribute(AnimatableElement::floatValue, 150f)
        assertEquals(1, interrupts)

        root.tickTransitions(50) // new transition starts, reads current value 50
        assertEquals(50f, element.floatValue, 1e-4f)
        root.tickTransitions(100) // half-way from 50 -> 150
        assertEquals(100f, element.floatValue, 1e-4f)
        root.tickTransitions(150)
        assertEquals(150f, element.floatValue, 1e-4f)
    }

    @Test
    fun selectionInterrupt_stopsRunningTransition() {
        val (root, selection) = setup(1)
        var interrupts = 0
        selection.transition().duration(100)
            .on(TransitionEvent.Interrupt) { interrupts++ }
            .attribute(AnimatableElement::floatValue, 100f)

        root.tickTransitions(0)
        assertTrue(root.hasPendingTransitions)
        selection.interrupt()
        assertEquals(1, interrupts)
        assertFalse(root.hasPendingTransitions)
    }

    @Test
    fun interruptBeforeStart_firesCancel() {
        val (root, selection) = setup(1)
        var cancels = 0
        var interrupts = 0
        selection.transition().delay(100).duration(100)
            .on(TransitionEvent.Cancel) { cancels++ }
            .on(TransitionEvent.Interrupt) { interrupts++ }
            .attribute(AnimatableElement::floatValue, 100f)

        root.tickTransitions(0) // scheduled, not started (delay 100)
        selection.interrupt()
        assertEquals(1, cancels)
        assertEquals(0, interrupts)
    }

    @Test
    fun customTween_isInvokedWithEasedFraction() {
        val (root, selection) = setup(1)
        val samples = mutableListOf<Float>()
        selection.transition().duration(100).ease(easeLinear).tween("sample") {
            { fraction -> samples.add(fraction) }
        }
        root.tickTransitions(0)
        root.tickTransitions(50)
        root.tickTransitions(100)
        assertEquals(listOf(0f, 0.5f, 1f), samples)
    }

    @Test
    fun schedulers_areIndependentPerRoot() {
        val (rootA, selectionA) = setup(1)
        val (rootB, selectionB) = setup(1)
        selectionA.transition().duration(100).attribute(AnimatableElement::floatValue, 1f)

        assertTrue(rootA.hasPendingTransitions)
        assertFalse(rootB.hasPendingTransitions)

        selectionB.transition().duration(100).attribute(AnimatableElement::floatValue, 1f)
        rootA.tickTransitions(0)
        rootA.tickTransitions(100)
        assertFalse(rootA.hasPendingTransitions)
        assertTrue(rootB.hasPendingTransitions)
    }
}
