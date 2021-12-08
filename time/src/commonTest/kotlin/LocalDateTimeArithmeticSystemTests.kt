package com.juul.krayon.time

import com.juul.krayon.time.LocalDateTimeArithmeticSystem.minus
import com.juul.krayon.time.LocalDateTimeArithmeticSystem.plus
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month.DECEMBER
import kotlinx.datetime.Month.FEBRUARY
import kotlinx.datetime.Month.JANUARY
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

class LocalDateTimeArithmeticSystemTests {

    @OptIn(ExperimentalTime::class)
    @Test
    fun plus_durationOverLeapSecond_ignoresLeapSecond() {
        val start = LocalDateTime(2016, DECEMBER, 31, 12, 30, 0)
        val end = LocalDateTime(2017, JANUARY, 1, 18, 30, 0)
        assertEquals(end, (start + 30.hours))
    }

    @Test
    fun minus_earlierDateWithEarlierTime_returnsPositiveDuration() {
        val left = LocalDateTime(2021, FEBRUARY, 11, 12, 0, 0)
        val right = LocalDateTime(2021, FEBRUARY, 10, 6, 0, 0)
        assertEquals(30.hours, left - right)
    }

    @Test
    fun minus_earlierDateWithLaterTime_returnsPositiveDuration() {
        val left = LocalDateTime(2021, FEBRUARY, 11, 12, 0, 0)
        val right = LocalDateTime(2021, FEBRUARY, 10, 18, 0, 0)
        assertEquals(18.hours, left - right)
    }

    @Test
    fun minus_laterDateWithLaterTime_returnsNegativeDuration() {
        val left = LocalDateTime(2021, FEBRUARY, 11, 12, 0, 0)
        val right = LocalDateTime(2021, FEBRUARY, 12, 18, 0, 0)
        assertEquals(-(30.hours), left - right)
    }

    @Test
    fun minus_laterDateWithEarlierTime_returnsNegativeDuration() {
        val left = LocalDateTime(2021, FEBRUARY, 11, 12, 0, 0)
        val right = LocalDateTime(2021, FEBRUARY, 12, 6, 0, 0)
        assertEquals(-(18.hours), left - right)
    }
}
