package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.Color
import com.juul.krayon.color.crimson
import com.juul.krayon.color.forestGreen
import com.juul.krayon.color.steelBlue
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.extent
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import kotlin.random.Random

data class Flower(
    val species: String,
    val petalLength: Float,
    val petalWidth: Float,
)

/** A deterministic, iris-like dataset of three clustered species. */
val flowers: List<Flower> = buildList {
    val random = Random(seed = 21)
    fun cluster(species: String, count: Int, length: Float, width: Float, spread: Float) {
        repeat(count) {
            add(
                Flower(
                    species = species,
                    petalLength = length + (random.nextFloat() - 0.5f) * spread * 2f,
                    petalWidth = width + (random.nextFloat() - 0.5f) * spread,
                ),
            )
        }
    }
    cluster("setosa", 30, length = 1.5f, width = 0.3f, spread = 0.4f)
    cluster("versicolor", 30, length = 4.3f, width = 1.3f, spread = 0.8f)
    cluster("virginica", 30, length = 5.6f, width = 2.0f, spread = 0.9f)
}

private val speciesColor: Map<String, Color> = mapOf(
    "setosa" to steelBlue,
    "versicolor" to crimson,
    "virginica" to forestGreen,
)

fun scatterplot(root: RootElement, width: Float, height: Float, data: List<Flower>) {
    val margin = Margin(top = 16f, right = 16f, bottom = 32f, left = 40f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    val x = scale()
        .domain(0f, data.extent { it.petalLength }.last())
        .range(0f, innerWidth)
    val y = scale()
        .domain(0f, data.extent { it.petalWidth }.last())
        .range(innerHeight, 0f)

    val body = root.asSelection()
        .selectAll(TransformElement.withKind("body"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "body" } }
        .each { transform = Transform.Translate(margin.left, margin.top) }

    body.selectAll(TransformElement.withKind("x-axis"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "x-axis" } }
        .each { transform = Transform.Translate(vertical = innerHeight) }
        .call(axisBottom(x))

    body.selectAll(GroupElement.withKind("y-axis"))
        .data(listOf(null))
        .join { append(GroupElement).each { kind = "y-axis" } }
        .call(axisLeft(y))

    body.selectAll(CircleElement)
        .data(data)
        .join(CircleElement)
        .each { (flower) ->
            centerX = x.scale(flower.petalLength)
            centerY = y.scale(flower.petalWidth)
            radius = 4f
            paint = Paint.Fill(checkNotNull(speciesColor[flower.species]).copy(alpha = 0xB3))
        }
}
