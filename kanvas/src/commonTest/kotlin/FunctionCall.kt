package com.juul.krayon.kanvas

import kotlin.reflect.KFunction

data class FunctionCall(
    val function: KFunction<*>,
    val arguments: List<Any?>,
)
