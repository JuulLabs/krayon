package com.juul.krayon.shape

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val TAU = (2 * PI).toFloat()

public fun pie(): Pie<Float> = Pie(value = { it })

public class Pie<T> internal constructor(
    private val value: (T) -> Float,
    startAngle: Float = 0f,
    endAngle: Float = TAU,
    padAngle: Float = 0f,
) {

    /** Overall start angle (start angle of the first arc). In radians, where `0` is at 12 o'clock and positive increases move clockwise. */
    public var startAngle: Float = startAngle
        private set

    /** Overall end angle (end angle of the last arc). In radians, where `0` is at 12 o'clock and positive increases move clockwise. */
    public var endAngle: Float = endAngle
        private set

    /** Angular separation between arcs in radians. */
    public var padAngle: Float = padAngle
        private set

    /** Returns a new [Pie] which can compute a numeric value for complex data types. */
    public fun <T2> value(value: (T2) -> Float): Pie<T2> = Pie(value, startAngle, endAngle, padAngle)

    public fun startAngle(value: Float): Pie<T> = apply { startAngle = value }
    public fun endAngle(value: Float): Pie<T> = apply { endAngle = value }
    public fun padAngle(value: Float): Pie<T> = apply { padAngle = value }

    public operator fun invoke(vararg data: T): List<Slice<T>> = this(data.toList())

    public operator fun invoke(data: List<T>): List<Slice<T>> {
        if (data.isEmpty()) return emptyList()

        val da = min(TAU, max(-TAU, endAngle - startAngle))
        val p = min(abs(da) / data.size, padAngle)
        val pa = p * (if (da < 0) -1 else 1)

        val values = data.map(value)
        check(values.all { it >= 0 }) {
            val index = values.indexOfFirst { it < 0 }
            "Values must be greater than or equal to zero, but got `${values[index]}` for item `${data[index]}`."
        }
        check(values.any { it > 0f }) { "At least one value must be greater than zero, but were all zero for items `$data`." }
        val sum = values.sum()

        var angle = startAngle
        val k = (da - data.size * pa) / sum
        return values.mapIndexed { index, value ->
            val endAngle = angle + (value * k) + pa
            Slice(
                data = data[index],
                value = value,
                index = index,
                startAngle = angle,
                endAngle = endAngle,
                padAngle = p,
            ).also {
                angle = endAngle
            }
        }
    }
}
