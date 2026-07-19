package com.juul.krayon.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull

class FormatSpecifierTests {

    @Test
    fun formatSpecifier_empty_hasExpectedDefaults() {
        val specifier = formatSpecifier("")
        assertEquals(" ", specifier.fill)
        assertEquals(">", specifier.align)
        assertEquals("-", specifier.sign)
        assertEquals("", specifier.symbol)
        assertFalse(specifier.zero)
        assertNull(specifier.width)
        assertFalse(specifier.comma)
        assertNull(specifier.precision)
        assertFalse(specifier.trim)
        assertEquals("", specifier.type)
    }

    @Test
    fun formatSpecifier_default_toStringNormalizes() {
        assertEquals(" >-d", format("d").toString())
        assertEquals(" >-", formatSpecifier("").toString())
    }

    @Test
    fun formatSpecifier_preservesUnknownType() {
        val specifier = formatSpecifier("q")
        assertFalse(specifier.trim)
        assertEquals("q", specifier.type)
    }

    @Test
    fun formatSpecifier_toStringReflectsMutations() {
        val specifier = formatSpecifier("")
        specifier.fill = "_"
        specifier.align = "^"
        specifier.sign = "+"
        specifier.symbol = "$"
        specifier.zero = true
        specifier.width = 12
        specifier.comma = true
        specifier.precision = 2
        specifier.type = "f"
        specifier.trim = true
        assertEquals("_^+$012,.2~f", specifier.toString())
        assertEquals("+$0,000,000,042", format(specifier)(42.0))
    }

    @Test
    fun formatSpecifier_clampsWidthAndPrecision() {
        val precision = formatSpecifier("")
        precision.precision = -1
        assertEquals(" >-.0", precision.toString())
        val width = formatSpecifier("")
        width.width = -1
        assertEquals(" >-1", width.toString())
    }

    @Test
    fun formatSpecifier_invalidThrows() {
        assertFailsWith<IllegalArgumentException> { formatSpecifier("foo") }
        assertFailsWith<IllegalArgumentException> { formatSpecifier(".-2s") }
        assertFailsWith<IllegalArgumentException> { formatSpecifier(".f") }
    }
}
