package com.juul.krayon.axis

import com.juul.krayon.axis.Edge.Bottom
import com.juul.krayon.axis.Edge.Left
import com.juul.krayon.axis.Edge.Right
import com.juul.krayon.axis.Edge.Top
import com.juul.krayon.color.Color
import com.juul.krayon.color.black
import com.juul.krayon.element.Element
import com.juul.krayon.element.LineElement
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Paint.Text.Alignment
import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.scale.ContinuousScale
import com.juul.krayon.scale.DoubleTicker
import com.juul.krayon.scale.FloatTicker
import com.juul.krayon.scale.InstantTicker
import com.juul.krayon.scale.LocalDateTimeTicker
import com.juul.krayon.scale.Ticker
import com.juul.krayon.scale.invoke
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.append
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.insert
import com.juul.krayon.selection.keyedData
import com.juul.krayon.selection.merge
import com.juul.krayon.selection.order
import com.juul.krayon.selection.remove
import com.juul.krayon.selection.select
import com.juul.krayon.selection.selectAll
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.jvm.JvmName
import kotlin.math.max

@JvmName("axisLeftFloat")
public fun axisLeft(scale: ContinuousScale<Float, Float>): ContinuousAxis<Float> = ContinuousAxis(Left, scale, FloatTicker)

@JvmName("axisTopFloat")
public fun axisTop(scale: ContinuousScale<Float, Float>): ContinuousAxis<Float> = ContinuousAxis(Top, scale, FloatTicker)

@JvmName("axisRightFloat")
public fun axisRight(scale: ContinuousScale<Float, Float>): ContinuousAxis<Float> = ContinuousAxis(Right, scale, FloatTicker)

@JvmName("axisBottomFloat")
public fun axisBottom(scale: ContinuousScale<Float, Float>): ContinuousAxis<Float> = ContinuousAxis(Bottom, scale, FloatTicker)

@JvmName("axisLeftDouble")
public fun axisLeft(scale: ContinuousScale<Double, Float>): ContinuousAxis<Double> = ContinuousAxis(Left, scale, DoubleTicker)

@JvmName("axisTopDouble")
public fun axisTop(scale: ContinuousScale<Double, Float>): ContinuousAxis<Double> = ContinuousAxis(Top, scale, DoubleTicker)

@JvmName("axisRightDouble")
public fun axisRight(scale: ContinuousScale<Double, Float>): ContinuousAxis<Double> = ContinuousAxis(Right, scale, DoubleTicker)

@JvmName("axisBottomDouble")
public fun axisBottom(scale: ContinuousScale<Double, Float>): ContinuousAxis<Double> = ContinuousAxis(Bottom, scale, DoubleTicker)

@JvmName("axisLeftLocalDateTime")
public fun axisLeft(scale: ContinuousScale<LocalDateTime, Float>): ContinuousAxis<LocalDateTime> = ContinuousAxis(Left, scale, LocalDateTimeTicker)

@JvmName("axisTopLocalDateTime")
public fun axisTop(scale: ContinuousScale<LocalDateTime, Float>): ContinuousAxis<LocalDateTime> = ContinuousAxis(Top, scale, LocalDateTimeTicker)

@JvmName("axisRightLocalDateTime")
public fun axisRight(scale: ContinuousScale<LocalDateTime, Float>): ContinuousAxis<LocalDateTime> = ContinuousAxis(Right, scale, LocalDateTimeTicker)

@JvmName("axisBottomLocalDateTime")
public fun axisBottom(scale: ContinuousScale<LocalDateTime, Float>): ContinuousAxis<LocalDateTime> = ContinuousAxis(Bottom, scale, LocalDateTimeTicker)

@JvmName("axisLeftInstant")
public fun axisLeft(scale: ContinuousScale<Instant, Float>): ContinuousAxis<Instant> = ContinuousAxis(Left, scale, InstantTicker)

@JvmName("axisTopInstant")
public fun axisTop(scale: ContinuousScale<Instant, Float>): ContinuousAxis<Instant> = ContinuousAxis(Top, scale, InstantTicker)

@JvmName("axisRightInstant")
public fun axisRight(scale: ContinuousScale<Instant, Float>): ContinuousAxis<Instant> = ContinuousAxis(Right, scale, InstantTicker)

@JvmName("axisBottomInstant")
public fun axisBottom(scale: ContinuousScale<Instant, Float>): ContinuousAxis<Instant> = ContinuousAxis(Bottom, scale, InstantTicker)

public fun Selection<*, *>.call(axis: ContinuousAxis<*>): Unit = axis.applySelection(this)

// TODO: Axis should support scales other than ContinuousScale, but since we don't have other scales yet...
public class ContinuousAxis<D : Comparable<D>> internal constructor(
    private val edge: Edge,
    private val scale: ContinuousScale<D, Float>,
    private val ticker: Ticker<D>,
) {
    public var tickSizeInner: Float = 6f
    public var tickSizeOuter: Float = 6f
    public var tickPadding: Float = 3f
    public var tickCount: Int = 5

    public var font: Font = Font(sansSerif)
    public var textSize: Float = 14f
    public var textColor: Color = black
    public var lineWidth: Float = 1f
    public var lineColor: Color = black
    public var formatter: (D) -> String = { it.toString() }

    private val k = if (edge == Top || edge == Left) -1 else 1
    private val isVertical = edge == Left || edge == Right

    public fun applySelection(selection: Selection<*, *>) {
        val values = ticker.ticks(scale.domain.minOf { it }, scale.domain.maxOf { it }, tickCount)
        val spacing = max(tickSizeInner, 0f) + tickPadding

        val range = scale.range
        val range0 = range.first()
        val range1 = range.last()

        val path = selection.selectAll(PathElement.withKind("domain"))
            .data(listOf(null))

        val tick = selection.selectAll(TransformElement.withKind("tick"))
            .keyedData(values) { (d) -> (d as? D)?.let(scale::scale) }
            .order()
        val tickEnter = tick.enter.append(TransformElement)
            .each { kind = "tick" }
        val tickExit = tick.exit

        val line = tick.select(LineElement)

        val text = tick.select(TextElement)

        val linePaint = Paint.Stroke(lineColor, lineWidth)
        val pathMerge = path.merge(
            path.enter.insert(PathElement, Element.withKind("tick"))
                .each { kind = "domain" }
        ).each { paint = linePaint }

        val tickMerge = tick.merge(tickEnter)

        val lineMerge = line.merge(
            tickEnter.append(LineElement).each {
                if (isVertical) {
                    endX = k * tickSizeInner
                } else {
                    endY = k * tickSizeInner
                }
            }
        ).each { paint = linePaint }

        val alignment = when (edge) {
            Left -> Alignment.Right
            Right -> Alignment.Left
            else -> Alignment.Center
        }
        val textPaint = Paint.Text(textColor, textSize, alignment, font)
        val textMerge = text.merge(
            tickEnter.append(TextElement).each {
                if (isVertical) {
                    x = k * spacing
                } else {
                    y = k * spacing
                }
                // Constants chosen from d3's em value for `dy`
                verticalAlign = when (edge) {
                    Top -> 0f
                    Left, Right -> 0.32f
                    Bottom -> 0.71f
                }
            }
        ).each { paint = textPaint }

        // TODO: animations will live here, once we support animations.

        tickExit.remove()

        pathMerge.each {
            this.path = Path {
                if (isVertical) {
                    moveTo(k * tickSizeOuter, range0)
                    lineTo(0f, range0)
                    lineTo(0f, range1)
                    lineTo(k * tickSizeOuter, range1)
                } else {
                    moveTo(range0, k * tickSizeOuter)
                    lineTo(range0, 0f)
                    lineTo(range1, 0f)
                    lineTo(range1, k * tickSizeOuter)
                }
            }
        }

        tickMerge.each { (d) ->
            this.transform = Transform.Translate(
                horizontal = if (isVertical) 0f else scale(d),
                vertical = if (isVertical) scale(d) else 0f
            )
        }

        lineMerge.each {
            if (isVertical) {
                endX = k * tickSizeInner
            } else {
                endY = k * tickSizeInner
            }
        }

        textMerge.each { (d) ->
            if (isVertical) {
                x = k * spacing
            } else {
                y = k * spacing
            }
            this.text = formatter(d)
        }
    }
}
