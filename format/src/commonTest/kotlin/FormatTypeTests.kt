package com.juul.krayon.format

import kotlin.test.Test
import kotlin.test.assertEquals

private const val MINUS = "\u2212"
private const val MICRO = "\u00b5"

class FormatTypeTests {

    @Test
    fun fixed_outputsFixedPointNotation() {
        assertEquals("0.5", format(".1f")(0.49))
        assertEquals("0.45", format(".2f")(0.449))
        assertEquals("0.445", format(".3f")(0.4449))
        assertEquals("0.44445", format(".5f")(0.444449))
        assertEquals("100.0", format(".1f")(100.0))
        assertEquals("100.00", format(".2f")(100.0))
        assertEquals("100.00000", format(".5f")(100.0))
        assertEquals("42.000000", format("f")(42.0))
    }

    @Test
    fun fixed_currencyWithCommaAndSign() {
        val f = format("+$,.2f")
        assertEquals("+$0.00", f(0.0))
        assertEquals("+$0.43", f(0.429))
        assertEquals("$MINUS$0.43", f(-0.429))
        assertEquals("$MINUS$1.00", f(-1.0))
        assertEquals("+$10,000.00", f(1e4))
    }

    @Test
    fun fixed_groupsThousands() {
        assertEquals("1,234,567.45", format("10,.2f")(1234567.449))
        assertEquals("12,345,678.445", format("10,.3f")(12345678.4449))
        assertEquals("123,456,789.44445", format("10,.5f")(123456789.444449))
        assertEquals("1,234,567.00", format("10,.2f")(1234567.0))
    }

    @Test
    fun fixed_negativeZeroFormatsAsZero() {
        assertEquals("0.000000", format("f")(-0.0))
        assertEquals("0.000000", format("f")(-1e-12))
        assertEquals("${MINUS}0.000000", format("+f")(-0.0))
        assertEquals("+0.000000", format("+f")(0.0))
        assertEquals("${MINUS}0.000000", format("+f")(-1e-12))
        assertEquals("+0.000000", format("+f")(1e-12))
    }

    @Test
    fun fixed_infinity() {
        assertEquals("${MINUS}Infinity", format("f")(Double.NEGATIVE_INFINITY))
        assertEquals("Infinity", format(",f")(Double.POSITIVE_INFINITY))
    }

    @Test
    fun exponential_outputsExponentNotation() {
        val f = format("e")
        assertEquals("0.000000e+0", f(0.0))
        assertEquals("4.200000e+1", f(42.0))
        assertEquals("4.200000e+7", f(42000000.0))
        assertEquals("${MINUS}4.000000e+0", f(-4.0))
        assertEquals("${MINUS}4.200000e+6", f(-4200000.0))
        assertEquals("4e+1", format(".0e")(42.0))
        assertEquals("4.200e+1", format(".3e")(42.0))
        assertEquals("${MINUS}1.000000e-12", format("1e")(-1e-12))
    }

    @Test
    fun general_outputsGeneralNotation() {
        assertEquals("0.05", format(".1g")(0.049))
        assertEquals("0.5", format(".1g")(0.49))
        assertEquals("0.45", format(".2g")(0.449))
        assertEquals("0.44445", format(".5g")(0.444449))
        assertEquals("1e+2", format(".1g")(100.0))
        assertEquals("1.0e+2", format(".2g")(100.0))
        assertEquals("100", format(".3g")(100.0))
        assertEquals("100.00", format(".5g")(100.0))
        assertEquals("100.20", format(".5g")(100.2))
        assertEquals("0.0020", format(".2g")(0.002))
    }

    @Test
    fun general_groupsThousands() {
        val f = format(",.12g")
        assertEquals("0.00000000000", f(0.0))
        assertEquals("42.0000000000", f(42.0))
        assertEquals("42,000,000.0000", f(42000000.0))
        assertEquals("420,000,000.000", f(420000000.0))
        assertEquals("${MINUS}4,200,000.00000", f(-4200000.0))
    }

    @Test
    fun rounded_roundsToSignificantDigits() {
        assertEquals("0.05", format(".1r")(0.049))
        assertEquals("${MINUS}0.05", format(".1r")(-0.049))
        assertEquals("0.5", format(".1r")(0.49))
        assertEquals("0.45", format(".2r")(0.449))
        assertEquals("1.00", format(".3r")(1.00))
        assertEquals("1.00", format(".3r")(0.9995))
        assertEquals("123.450", format("r")(123.45))
        assertEquals("100", format(".1r")(123.45))
        assertEquals("120", format(".2r")(123.45))
        assertEquals("123", format(".3r")(123.45))
        assertEquals("123.5", format(".4r")(123.45))
        assertEquals("123.45", format(".5r")(123.45))
        assertEquals("0.09", format(".1r")(0.09))
        assertEquals("0.00000001", format(".1r")(0.0000000129))
        assertEquals("0.000000013", format(".2r")(0.0000000129))
        assertEquals("0.9999999999", format(".10r")(0.9999999999))
    }

    @Test
    fun rounded_roundsZeroAndVerySmall() {
        assertEquals("0", format(".2r")(0.0))
        assertEquals("0", format("r")(0.0))
        assertEquals("0.00000000000000000000010", format(".2r")(1e-22))
    }

    @Test
    fun default_usesSignificantPrecisionAndTrims() {
        assertEquals("5", format(".1")(4.9))
        assertEquals("0.5", format(".1")(0.49))
        assertEquals("4.9", format(".2")(4.9))
        assertEquals("0.449", format(".3")(0.449))
        assertEquals("0.445", format(".3")(0.4449))
        assertEquals("10", format(".5")(10.0))
        assertEquals("21010", format(".5")(21010.0))
        assertEquals("1.1", format(".5")(1.10001))
        assertEquals("1.1e+6", format(".5")(1.10001e6))
        assertEquals("1.10001", format(".6")(1.10001))
        assertEquals("1", format(".5")(1.00001))
        assertEquals("1e+6", format(".5")(1.00001e6))
    }

    @Test
    fun currency_default() {
        val f = format("$")
        assertEquals("$0", f(0.0))
        assertEquals("$0.042", f(0.042))
        assertEquals("$4.2", f(4.2))
        assertEquals("$MINUS$4.2", f(-4.2))
    }

    @Test
    fun currency_parenthesesForNegatives() {
        val f = format("($")
        assertEquals("$0", f(0.0))
        assertEquals("$4.2", f(4.2))
        assertEquals("($0.042)", f(-0.042))
        assertEquals("($4.2)", f(-4.2))
    }

    @Test
    fun decimal_zeroFillAndSpaceFill() {
        assertEquals("00000042", format("08d")(42.0))
        assertEquals("42000000", format("08d")(42000000.0))
        assertEquals("${MINUS}0000004", format("08d")(-4.0))
        assertEquals("${MINUS}4200000", format("08d")(-4200000.0))
        assertEquals(" ".repeat(6) + "42", format("8d")(42.0))
        assertEquals(" ".repeat(6) + "${MINUS}4", format("8d")(-4.0))
    }

    @Test
    fun decimal_underscoreFill() {
        assertEquals("_______0", format("_>8d")(0.0))
        assertEquals("______42", format("_>8d")(42.0))
        assertEquals("______${MINUS}4", format("_>8d")(-4.0))
        assertEquals("_____${MINUS}42", format("_>8d")(-42.0))
    }

    @Test
    fun decimal_zeroFillWithSignAndGroup() {
        val f = format("+08,d")
        assertEquals("+0,000,000", f(0.0))
        assertEquals("+0,000,042", f(42.0))
        assertEquals("+42,000,000", f(42000000.0))
        assertEquals("${MINUS}0,000,004", f(-4.0))
        assertEquals("${MINUS}4,200,000", f(-4200000.0))
    }

    @Test
    fun decimal_roundsAndUsesZeroPrecision() {
        assertEquals("4", format("d")(4.2))
        assertEquals("42", format(".2d")(42.0))
        assertEquals("${MINUS}4", format(".2d")(-4.2))
        assertEquals("0", format("1d")(-0.0))
        assertEquals("0", format("1d")(-1e-12))
    }

    @Test
    fun decimal_groupsVeryLargeNumbers() {
        val f = format(",d")
        assertEquals("1,000,000,000,000,000,000,000", f(1e21))
        assertEquals("1,300,000,000,000,000,000,000,000,000", f(1.3e27))
    }

    @Test
    fun decimal_alignment() {
        assertEquals("0 ", format("<2,d")(0.0))
        assertEquals(" 0", format(">2,d")(0.0))
        assertEquals(" 0 ", format("^3,d")(0.0))
        assertEquals(" ".repeat(8) + "1,000" + " ".repeat(8), format("^21,d")(1000.0))
    }

    @Test
    fun decimal_padAfterSign() {
        assertEquals("+0", format("=+1,d")(0.0))
        assertEquals("+ 0", format("=+3,d")(0.0))
        assertEquals("+$" + " ".repeat(2) + "0", format("=+$5,d")(0.0))
    }

    @Test
    fun hexadecimal() {
        assertEquals("deadbeef", format("x")(0xdeadbeef.toDouble()))
        assertEquals("0xdeadbeef", format("#x")(0xdeadbeef.toDouble()))
        assertEquals("de,adb,eef", format(",x")(0xdeadbeef.toDouble()))
        assertEquals("0xade,adb,eef", format("#,x")(0xadeadbeef.toDouble()))
        assertEquals("+0xdeadbeef", format("+#x")(0xdeadbeef.toDouble()))
        assertEquals("${MINUS}0xdeadbeef", format("+#x")(-(0xdeadbeef.toDouble())))
        assertEquals("$" + "de,adb,eef", format("$,x")(0xdeadbeef.toDouble()))
        assertEquals("deadbeef", format(".2x")(0xdeadbeef.toDouble()))
        assertEquals("2", format("x")(2.4))
        assertEquals("0", format("x")(-0.0))
        assertEquals("${MINUS}eee", format("x")(-0xeee.toDouble()))
        assertEquals("DEADBEEF", format("X")(0xdeadbeef.toDouble()))
        assertEquals("0xDEADBEEF", format("#X")(0xdeadbeef.toDouble()))
        assertEquals("${MINUS}EEE", format("X")(-0xeee.toDouble()))
    }

    @Test
    fun hexadecimal_widthConsidersPrefix() {
        assertEquals("000000000000deadbeef", format("020x")(0xdeadbeef.toDouble()))
        assertEquals("0x0000000000deadbeef", format("#020x")(0xdeadbeef.toDouble()))
    }

    @Test
    fun binaryAndOctal() {
        assertEquals("1010", format("b")(10.0))
        assertEquals("0b1010", format("#b")(10.0))
        assertEquals("12", format("o")(10.0))
        assertEquals("0o12", format("#o")(10.0))
    }

    @Test
    fun percent() {
        val f = format("p")
        assertEquals("0.123000%", f(0.00123))
        assertEquals("12.3000%", f(0.123))
        assertEquals("123.000%", f(1.23))
        assertEquals("${MINUS}12.3000%", f(-0.123))
        val g = format("+.2p")
        assertEquals("+0.12%", g(0.00123))
        assertEquals("+12%", g(0.123))
        assertEquals("${MINUS}120%", g(-1.23))
    }

    @Test
    fun n_isAliasForCommaG() {
        assertEquals("123,457", format("n")(123456.78))
        assertEquals("123,457", format(",g")(123456.78))
        val f = format(".12n")
        assertEquals("42,000,000.0000", f(42000000.0))
        assertEquals("1.00000000000e+21", f(1e21))
    }

    @Test
    fun character() {
        assertEquals("42", format("c")(42.0))
        assertEquals("3.14", format("c")(3.14))
    }

    @Test
    fun siPrefix_defaultPrecision() {
        val f = format("s")
        assertEquals("0.00000", f(0.0))
        assertEquals("1.00000", f(1.0))
        assertEquals("100.000", f(100.0))
        assertEquals("999.500", f(999.5))
        assertEquals("999.500k", f(999500.0))
        assertEquals("1.00000k", f(1000.0))
        assertEquals("1.50050k", f(1500.5))
        assertEquals("10.0000$MICRO", f(0.00001))
        assertEquals("1.00000$MICRO", f(0.000001))
        assertEquals("Infinity", f(Double.POSITIVE_INFINITY))
        assertEquals("NaN", f(Double.NaN))
    }

    @Test
    fun siPrefix_withPrecision() {
        val f1 = format(".3s")
        assertEquals("0.00", f1(0.0))
        assertEquals("100", f1(100.0))
        assertEquals("1.00k", f1(999.5))
        assertEquals("1.00M", f1(999500.0))
        assertEquals("1.50k", f1(1500.5))
        assertEquals("146M", f1(145500000.0))
        assertEquals("146M", f1(145999999.99999347))
        assertEquals("100Y", f1(1e26))
        assertEquals("1.00$MICRO", f1(0.000001))
        assertEquals("10.0m", f1(0.009995))
        val f2 = format(".4s")
        assertEquals("999.5", f2(999.5))
        assertEquals("999.5k", f2(999500.0))
        assertEquals("9.995m", f2(0.009995))
    }

    @Test
    fun siPrefix_yoctoAndYotta() {
        val f = format(".8s")
        assertEquals("0.0000013y", f(1.29e-30))
        assertEquals("1.2900000y", f(1.29e-24))
        assertEquals("129.00000y", f(1.29e-22))
        assertEquals("1.2900000z", f(1.29e-21))
        assertEquals("1.2300000Z", f(1.23e21))
        assertEquals("123.00000Z", f(1.23e23))
        assertEquals("1.2300000Y", f(1.23e24))
        assertEquals("1230000.0Y", f(1.23e30))
        assertEquals("${MINUS}1.2900000y", f(-1.29e-24))
        assertEquals("${MINUS}1230000.0Y", f(-1.23e30))
    }

    @Test
    fun siPrefix_currency() {
        val f2 = format("$.3s")
        assertEquals("$0.00", f2(0.0))
        assertEquals("$10.0", f2(10.0))
        assertEquals("$1.00k", f2(999.5))
        assertEquals("$146M", f2(145500000.0))
        assertEquals("$1.00$MICRO", f2(0.000001))
    }

    @Test
    fun siPrefix_consistentPrecision() {
        val f = format(".0s")
        assertEquals("10$MICRO", f(1e-5))
        assertEquals("1m", f(1e-3))
        assertEquals("100m", f(1e-1))
        assertEquals("1", f(1e0))
        assertEquals("1k", f(1e3))
        assertEquals("100k", f(1e5))
    }

    @Test
    fun siPrefix_zeroFillGroupsThousands() {
        assertEquals("000,000,000,042.0000", format("020,s")(42.0))
        assertEquals("00,000,000,042.0000T", format("020,s")(42e12))
        assertEquals("42,000,000Y", format(",s")(42e30))
    }
}
