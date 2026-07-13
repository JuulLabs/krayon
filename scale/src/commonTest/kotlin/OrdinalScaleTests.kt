package com.juul.krayon.scale

import kotlin.test.Test
import kotlin.test.assertEquals

class OrdinalScaleTests {

    @Test
    fun ordinal_mapsDomainToRangeByIndex() {
        val scale = ordinalScale(domain = listOf("a", "b", "c"), range = listOf("red", "green", "blue"))
        assertEquals("red", scale.scale("a"))
        assertEquals("green", scale.scale("b"))
        assertEquals("blue", scale.scale("c"))
    }

    @Test
    fun ordinal_wrapsWhenRangeShorterThanDomain() {
        val scale = ordinalScale(domain = listOf("a", "b", "c"), range = listOf(0, 1))
        assertEquals(0, scale.scale("a"))
        assertEquals(1, scale.scale("b"))
        assertEquals(0, scale.scale("c"))
    }

    @Test
    fun ordinal_implicitlyExtendsDomainForUnknownInput() {
        val scale = ordinalScale(domain = listOf("a"), range = listOf(0, 1, 2))
        assertEquals(0, scale.scale("a"))
        assertEquals(1, scale.scale("b"))
        assertEquals(2, scale.scale("c"))
        assertEquals(listOf("a", "b", "c"), scale.domain)
    }

    @Test
    fun ordinal_returnsUnknownValueWhenConfigured() {
        val scale = ordinalScale(domain = listOf("a", "b"), range = listOf(10, 20)).unknown(-1)
        assertEquals(10, scale.scale("a"))
        assertEquals(-1, scale.scale("zzz"))
        assertEquals(listOf("a", "b"), scale.domain)
    }

    @Test
    fun ordinal_deduplicatesDomain() {
        val scale = ordinalScale(domain = listOf("a", "a", "b"), range = listOf(0, 1, 2))
        assertEquals(listOf("a", "b"), scale.domain)
        assertEquals(1, scale.scale("b"))
    }
}
