package com.juul.krayon.time

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
public operator fun LocalDateTime.plus(duration: Duration): LocalDateTime =
    (this.toInstant(UTC) + duration).toLocalDateTime(UTC)

@OptIn(ExperimentalTime::class)
public operator fun LocalDateTime.minus(duration: Duration): LocalDateTime =
    (this.toInstant(UTC) - duration).toLocalDateTime(UTC)

@OptIn(ExperimentalTime::class)
public operator fun LocalDateTime.minus(other: LocalDateTime): Duration =
    this.toInstant(UTC) - other.toInstant(UTC)
