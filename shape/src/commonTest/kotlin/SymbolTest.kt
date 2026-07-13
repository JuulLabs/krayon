package com.juul.krayon.shape

import com.juul.krayon.kanvas.svg.toPath
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class SymbolTest {

    @Test
    fun symbol_default_isCircleWithSize64() {
        val r = sqrt(64f / PI.toFloat())
        val ops = symbol<Unit>().render(Unit).ops()
        assertEquals("M", ops[0].name)
        assertEquals(r, ops[0].args[0], absoluteTolerance = 1e-4f)
        assertEquals(0f, ops[0].args[1], absoluteTolerance = 1e-4f)
        assertEquals(listOf("M", "A", "A"), ops.map { it.name })
    }

    @Test
    fun symbol_typeAndSizeAccessors_produceExpectedPath() {
        val fromGenerator = symbol<Float>().type(Cross).size { (datum) -> datum }.render(20f)
        assertPathEquals(Cross.render(20f), fromGenerator)
    }

    @Test
    fun circle_hasExpectedRadiusAndArcs() {
        val ops = Circle.render(20f).ops()
        val r = sqrt(20f / PI.toFloat())
        assertEquals(r, ops[0].args[0], absoluteTolerance = 1e-4f)
        assertEquals(2, ops.count { it.name == "A" })
    }

    @Test
    fun cross_generatesExpectedPath() {
        assertPathEquals("M-3,-1L-1,-1L-1,-3L1,-3L1,-1L3,-1L3,1L1,1L1,3L-1,3L-1,1L-3,1Z".toPath(), Cross.render(20f))
    }

    @Test
    fun diamond_generatesExpectedPath() {
        assertPathEquals("M0,-2.942831L1.699044,0L0,2.942831L-1.699044,0Z".toPath(), Diamond.render(10f))
    }

    @Test
    fun square_generatesExpectedPath() {
        assertPathEquals("M-1,-1L1,-1L1,1L-1,1Z".toPath(), Square.render(4f))
        assertPathEquals("M-2,-2L2,-2L2,2L-2,2Z".toPath(), Square.render(16f))
    }

    @Test
    fun star_generatesExpectedPath() {
        assertPathEquals(
            (
                "M0,-2.984649L0.670095,-0.922307L2.838570,-0.922307L1.084237,0.352290L1.754333,2.414632" +
                    "L0,1.140035L-1.754333,2.414632L-1.084237,0.352290L-2.838570,-0.922307L-0.670095,-0.922307Z"
            ).toPath(),
            Star.render(10f),
        )
    }

    @Test
    fun triangle_generatesExpectedPath() {
        assertPathEquals("M0,-2.774528L2.402811,1.387264L-2.402811,1.387264Z".toPath(), Triangle.render(10f))
    }

    @Test
    fun wye_generatesExpectedPath() {
        assertPathEquals(
            (
                "M0.853360,0.492688L0.853360,2.199408L-0.853360,2.199408L-0.853360,0.492688L-2.331423,-0.360672" +
                    "L-1.478063,-1.838735L0,-0.985375L1.478063,-1.838735L2.331423,-0.360672Z"
            ).toPath(),
            Wye.render(10f),
        )
    }

    @Test
    fun plus_generatesExpectedPath() {
        assertPathEquals("M-3.714814,0L3.714814,0M0,3.714814L0,-3.714814".toPath(), Plus.render(20f))
    }

    @Test
    fun times_generatesExpectedPath() {
        assertPathEquals("M-2.647561,-2.647561L2.647561,2.647561M-2.647561,2.647561L2.647561,-2.647561".toPath(), Times.render(20f))
    }

    @Test
    fun asterisk_generatesExpectedPath() {
        assertPathEquals(
            "M0,2.705108L0,-2.705108M-2.342692,-1.352554L2.342692,1.352554M-2.342692,1.352554L2.342692,-1.352554".toPath(),
            Asterisk.render(20f),
        )
    }

    @Test
    fun registries_containExpectedSymbols() {
        assertEquals(listOf(Circle, Cross, Diamond, Square, Star, Triangle, Wye), symbolsFill)
        assertEquals(listOf(Circle, Plus, Times, Asterisk), symbolsStroke)
    }
}
