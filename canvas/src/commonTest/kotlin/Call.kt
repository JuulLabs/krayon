package com.juul.krayon.canvas

import kotlin.reflect.KCallable

data class Call(
    val callable: KCallable<*>,
    val arguments: List<Any?>,
)
