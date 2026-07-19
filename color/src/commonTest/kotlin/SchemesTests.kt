package com.juul.krayon.color

import kotlin.test.Test
import kotlin.test.assertEquals

class SchemesTests {

    @Test
    fun schemeCategory10_matchesD3() {
        assertEquals(
            listOf("#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd", "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"),
            schemeCategory10.map { it.toHexString() },
        )
    }

    @Test
    fun schemeTableau10_startsWithExpectedColors() {
        assertEquals("#4e79a7", schemeTableau10.first().toHexString())
        assertEquals("#f28e2c", schemeTableau10[1].toHexString())
        assertEquals(10, schemeTableau10.size)
    }

    @Test
    fun schemeSet1_matchesD3() {
        assertEquals("#e41a1c", schemeSet1.first().toHexString())
        assertEquals(9, schemeSet1.size)
    }

    @Test
    fun sequentialAndDivergingSchemes_haveExpectedEndpoints() {
        assertEquals("#f7fbff", schemeBlues.first().toHexString())
        assertEquals("#08306b", schemeBlues.last().toHexString())
        assertEquals("#67001f", schemeRdBu.first().toHexString())
        assertEquals("#053061", schemeRdBu.last().toHexString())
        assertEquals(256, schemeViridis.size)
        assertEquals("#440154", schemeViridis.first().toHexString())
        assertEquals("#fde725", schemeViridis.last().toHexString())
    }
}
