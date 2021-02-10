package com.juul.krayon.chart.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class RectangularDataSetTests {

    private fun <T> transposeOf(list: List<List<T>>) = with(RectangularClusteredDataSet) { list.transposeOf() }

    @Test
    fun testTransposeRejectsJaggedLists() {
        assertFails { transposeOf(listOf(listOf(1), emptyList())) }
        assertFails { transposeOf(listOf(emptyList(), listOf(1))) }
    }

    @Test
    fun testTransposeWorksFor0x0() {
        assertEquals(emptyList(), transposeOf(emptyList<List<Any>>()))
    }

    @Test
    fun testTransposeWorksFor3x0() {
        assertEquals(emptyList(), transposeOf(listOf(emptyList<Any>(), emptyList(), emptyList())))
    }

    @Test
    fun testTransposeWorksFor1x1() {
        val original = listOf(listOf("0,0"))
        val expected = listOf(listOf("0,0"))
        assertEquals(expected, transposeOf(original))
    }

    @Test
    fun testTransposeWorksFor2x2() {
        val original = listOf(
            listOf("0,0", "1,0"),
            listOf("0,1", "1,1")
        )
        val expected = listOf(
            listOf("0,0", "0,1"),
            listOf("1,0", "1,1")
        )
        assertEquals(expected, transposeOf(original))
    }

    @Test
    fun testTransposeWorksFor3x3() {
        val original = listOf(
            listOf("0,0", "1,0", "2,0"),
            listOf("0,1", "1,1", "2,1"),
            listOf("0,2", "1,2", "2,2")
        )
        val expected = listOf(
            listOf("0,0", "0,1", "0,2"),
            listOf("1,0", "1,1", "1,2"),
            listOf("2,0", "2,1", "2,2")
        )
        assertEquals(expected, transposeOf(original))
    }

    @Test
    fun testTransposeWorksFor3x2() {
        val original = listOf(
            listOf("0,0", "1,0", "2,0"),
            listOf("0,1", "1,1", "2,1"),
        )
        val expected = listOf(
            listOf("0,0", "0,1"),
            listOf("1,0", "1,1"),
            listOf("2,0", "2,1")
        )
        assertEquals(expected, transposeOf(original))
    }
}
