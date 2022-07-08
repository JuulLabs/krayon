package com.juul.krayon.kanvas

/**
 * Used to bridge the gap between a [Path] and the platform type required by a [Kanvas].
 *
 * Ultimately, [Path] uses instances of this class as a [Map] key, so instances _must_ correctly
 * implement [hashCode]. This is automatic when implementations take the form of an `object` instead
 * of a `class`.
 */
public interface PathTypeMarker<P : Any> {
    public val builder: PathBuilder<P>
}
