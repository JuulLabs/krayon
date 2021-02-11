package com.juul.krayon.chart

internal fun checkRectangular(listOfLists: List<List<*>>) {
    if (listOfLists.isEmpty()) return

    val expectedLength = listOfLists.first().size
    for (index in 1 until listOfLists.size) {
        val innerListLength = listOfLists[index].size
        check(innerListLength == expectedLength) {
            "List is not rectangular. Found inner list at index $index with length $innerListLength, but previous lists had length $expectedLength."
        }
    }
}
