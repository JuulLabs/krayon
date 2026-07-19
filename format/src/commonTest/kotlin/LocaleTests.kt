package com.juul.krayon.format

import kotlin.test.Test
import kotlin.test.assertEquals

private const val MINUS = "\u2212"

class LocaleTests {

    @Test
    fun locale_observesDecimalPoint() {
        assertEquals("002|00", FormatLocale(FormatLocaleDefinition(decimal = "|")).format("06.2f")(2.0))
        assertEquals("002/00", FormatLocale(FormatLocaleDefinition(decimal = "/")).format("06.2f")(2.0))
    }

    @Test
    fun locale_observesCurrencyPrefixAndSuffix() {
        assertEquals(
            "\u0e3f02.00",
            FormatLocale(FormatLocaleDefinition(decimal = ".", currencyPrefix = "\u0e3f")).format("$06.2f")(2.0),
        )
        assertEquals(
            "02.00\u0e3f",
            FormatLocale(FormatLocaleDefinition(decimal = ".", currencySuffix = "\u0e3f")).format("$06.2f")(2.0),
        )
    }

    @Test
    fun locale_currencySuffixAfterSiSuffix() {
        assertEquals(
            "1,20G \u20ac",
            FormatLocale(FormatLocaleDefinition(decimal = ",", currencySuffix = " \u20ac")).format("$.3s")(1.2e9),
        )
    }

    @Test
    fun locale_noGroupingWithoutDefinition() {
        assertEquals("000000002.00", FormatLocale(FormatLocaleDefinition(decimal = ".")).format("012,.2f")(2.0))
    }

    @Test
    fun locale_observesGroupSizes() {
        assertEquals(
            "0,000,002.00",
            FormatLocale(FormatLocaleDefinition(decimal = ".", grouping = listOf(3), thousands = ",")).format("012,.2f")(2.0),
        )
        assertEquals(
            "0,00,00,02.00",
            FormatLocale(FormatLocaleDefinition(decimal = ".", grouping = listOf(2), thousands = ",")).format("012,.2f")(2.0),
        )
        assertEquals(
            "00,000,02.00",
            FormatLocale(FormatLocaleDefinition(decimal = ".", grouping = listOf(2, 3), thousands = ",")).format("012,.2f")(2.0),
        )
        assertEquals(
            "10,00,00,00,00,000",
            FormatLocale(
                FormatLocaleDefinition(decimal = ".", grouping = listOf(3, 2, 2, 2, 2, 2, 2), thousands = ","),
            ).format(",d")(1e12),
        )
    }

    @Test
    fun locale_indianNumberingSystem() {
        val indian = FormatLocale(
            FormatLocaleDefinition(decimal = ".", grouping = listOf(3, 2, 2, 2, 2, 2, 2), thousands = ","),
        ).format(",")
        assertEquals("1,000", indian(1000.0))
        assertEquals("1,00,000", indian(100000.0))
        assertEquals("10,00,000", indian(1000000.0))
        assertEquals("1,00,00,000", indian(10000000.0))
        assertEquals("1,00,00,000.4543", indian(10000000.4543))
        assertEquals("${MINUS}1,00,00,000", indian(-10000000.0))
    }

    @Test
    fun locale_observesThousandsSeparator() {
        assertEquals(
            "0 000 002.00",
            FormatLocale(FormatLocaleDefinition(decimal = ".", grouping = listOf(3), thousands = " ")).format("012,.2f")(2.0),
        )
    }

    @Test
    fun locale_observesPercentSign() {
        assertEquals(
            "200.00!",
            FormatLocale(FormatLocaleDefinition(decimal = ".", percent = "!")).format("06.2%")(2.0),
        )
    }

    @Test
    fun locale_observesMinusSign() {
        assertEquals("-02.00", FormatLocale(FormatLocaleDefinition(decimal = ".", minus = "-")).format("06.2f")(-2.0))
        assertEquals("${MINUS}02.00", FormatLocale(FormatLocaleDefinition(decimal = ".", minus = "\u2212")).format("06.2f")(-2.0))
        assertEquals("\u2796" + "02.00", FormatLocale(FormatLocaleDefinition(decimal = ".", minus = "\u2796")).format("06.2f")(-2.0))
        assertEquals("${MINUS}02.00", FormatLocale(FormatLocaleDefinition(decimal = ".")).format("06.2f")(-2.0))
    }

    @Test
    fun locale_observesNan() {
        assertEquals("   N/A", FormatLocale(FormatLocaleDefinition(nan = "N/A")).format("6.2f")(Double.NaN))
    }

    @Test
    fun locale_frenchDefault() {
        val french = FormatLocale(
            FormatLocaleDefinition(
                decimal = ",",
                thousands = ".",
                grouping = listOf(3),
                currencySuffix = "\u00a0\u20ac",
                percent = "\u202f%",
            ),
        )
        assertEquals("12.345.678,90\u00a0\u20ac", french.format("$,.2f")(12345678.90))
        assertEquals("1.234.567.890\u202f%", french.format(",.0%")(12345678.90))
    }

    @Test
    fun locale_numerals() {
        val arabic = FormatLocale(
            FormatLocaleDefinition(
                numerals = listOf("\u0660", "\u0661", "\u0662", "\u0663", "\u0664", "\u0665", "\u0666", "\u0667", "\u0668", "\u0669"),
            ),
        )
        assertEquals("\u0661\u0662\u0663", arabic.format("d")(123.0))
    }
}
