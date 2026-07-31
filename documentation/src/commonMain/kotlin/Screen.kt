package com.juul.krayon.documentation

/** Top-level destinations of the documentation site. */
enum class Screen(val route: String, val title: String) {
    Home("home", "What is Krayon?"),
    GettingStarted("getting-started", "Getting started"),
    Selections("concepts/selections", "Selections"),
    Scales("concepts/scales", "Scales"),
    Shapes("concepts/shapes", "Shapes"),
    Axes("concepts/axes", "Axes"),
    Hierarchy("concepts/hierarchy", "Hierarchy"),
    ColorConcept("concepts/color", "Color & interpolation"),
    Rendering("concepts/rendering", "Elements & rendering"),
    Interaction("concepts/interaction", "Interaction & animation"),
    D3("d3", "Krayon for D3 users"),
    Gallery("gallery", "Gallery"),
    ;

    companion object {
        fun fromRoute(route: String?): Screen? = entries.firstOrNull { it.route == route }
    }
}

/** Route of the gallery detail screen, parameterized by [com.juul.krayon.documentation.samples.Sample.id]. */
const val SAMPLE_DETAIL_ROUTE: String = "gallery/{sampleId}"

fun sampleDetailRoute(sampleId: String): String = "gallery/$sampleId"
