package com.juul.krayon.canvas

import kotlin.reflect.KFunction

data class FunctionCall(
    val function: KFunction<*>,
    val arguments: List<Any?>,
)
