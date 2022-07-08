package com.juul.krayon.shape

public data class Slice<T>(
    val data: T,
    val value: Float,
    val index: Int,
    val startAngle: Float,
    val endAngle: Float,
    val padAngle: Float,
)
