package com.juul.krayon.interpolate

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

// This functionality doesn't exist in KotlinX DateTime because the specifics of different use-cases
// affect the desired behavior. See: https://github.com/Kotlin/kotlinx-datetime/issues/66

@OptIn(ExperimentalTime::class)
internal operator fun LocalDateTime.minus(other: LocalDateTime): Duration =
    this.toInstant(TimeZone.UTC) - other.toInstant(TimeZone.UTC)

@OptIn(ExperimentalTime::class)
internal operator fun LocalDateTime.plus(duration: Duration): LocalDateTime =
    (this.toInstant(TimeZone.UTC) + duration).toLocalDateTime(TimeZone.UTC)
