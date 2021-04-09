package com.juul.krayon.kanvas

import kotlin.reflect.KCallable

data class Call(
    val callable: KCallable<*>,
    val arguments: List<Any?>,
)
