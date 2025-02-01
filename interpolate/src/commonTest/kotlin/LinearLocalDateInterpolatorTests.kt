package com.juul.krayon.interpolate

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearLocalDateInterpolatorTests {

    @Test
    fun invert_thenInterpolate_datesMatch() {
        val start = LocalDate(2000, 1, 1)
        val end = LocalDate(2000, 12, 31)
        val interpolator = LinearLocalDateInterpolator(start, end)

        var current: LocalDate = start
        do {
            val fraction = interpolator.invert(current)
            val interpolated = interpolator.interpolate(fraction)
            assertEquals(
                expected = current,
                actual = interpolated,
            )
            current += DatePeriod(days = 1)
        } while (current <= end)
    }

    @Test
    fun invert_halfWayThroughWeek_returns0point5() {
        val interpolator = LinearLocalDateInterpolator(
            start = LocalDate(2000, 1, 1),
            stop = LocalDate(2000, 1, 7),
        )

        assertEquals(
            expected = 0.5f,
            actual = interpolator.invert(LocalDate(2000, 1, 4)),
        )
    }

    // > Every year that is exactly divisible by four is a leap year, except for years that are
    // > exactly divisible by 100, but these centurial years are leap years if they are exactly
    // > divisible by 400.
    // https://en.wikipedia.org/wiki/Leap_year
    //
    // > July 2 is the 183rd day of the year (184th in leap years) in the Gregorian calendar; 182
    // > days remain until the end of the year. This date marks the halfway point of the year.
    // https://en.wikipedia.org/wiki/July_2
    //
    // The year 2012 is **not** a leap year, therefor: July 2 is the halfway point of that year.
    @Test
    fun interpolate_halfWayThroughYear2012_returnsJuly2() {
        val interpolator = LinearLocalDateInterpolator(
            start = LocalDate(2012, 1, 1),
            stop = LocalDate(2012, 12, 31),
        )

        assertEquals(
            expected = LocalDate(2012, 7, 2),
            actual = interpolator.interpolate(0.5f),
        )
    }
}
