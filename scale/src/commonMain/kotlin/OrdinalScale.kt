package com.juul.krayon.scale

/**
 * Maps a discrete [domain] to a discrete [range], mirroring [d3's ordinal scale](https://github.com/d3/d3-scale#ordinal-scales).
 *
 * If an input is not already in the domain and no [unknown] value has been configured, the scale operates in *implicit*
 * mode: the input is appended to the domain and assigned the next value from the range (wrapping when the range is
 * shorter than the domain). Configuring an [unknown] value disables implicit extension and returns that value instead.
 */
public class OrdinalScale<D, R> internal constructor(
    domain: List<D>,
    public val range: List<R>,
    private val implicit: Boolean,
    private val unknownValue: R?,
) : Scale<D, R> {

    init {
        require(range.isNotEmpty()) { "Range must consist of at least 1 value." }
    }

    private val index = LinkedHashMap<D, Int>()
    private val mutableDomain = ArrayList<D>()

    init {
        for (value in domain) {
            if (!index.containsKey(value)) {
                index[value] = mutableDomain.size
                mutableDomain.add(value)
            }
        }
    }

    /** The domain values in insertion order. In implicit mode this grows as new inputs are encountered. */
    public val domain: List<D> get() = mutableDomain.toList()

    override fun scale(input: D): R {
        var i = index[input]
        if (i == null) {
            if (!implicit) {
                @Suppress("UNCHECKED_CAST")
                return unknownValue as R
            }
            i = mutableDomain.size
            index[input] = i
            mutableDomain.add(input)
        }
        return range[i % range.size]
    }

    public fun domain(domain: List<D>): OrdinalScale<D, R> = OrdinalScale(domain, range, implicit, unknownValue)

    public fun <R2> range(range: List<R2>): OrdinalScale<D, R2> = OrdinalScale(domain, range, implicit = true, unknownValue = null)

    /** Returns a copy that yields [value] for inputs outside the domain instead of extending the domain. */
    public fun unknown(value: R): OrdinalScale<D, R> = OrdinalScale(domain, range, implicit = false, unknownValue = value)
}

/** Creates an [OrdinalScale] in implicit mode with the given [domain] and [range]. */
public fun <D, R> ordinalScale(
    domain: List<D> = emptyList(),
    range: List<R>,
): OrdinalScale<D, R> = OrdinalScale(domain, range, implicit = true, unknownValue = null)
