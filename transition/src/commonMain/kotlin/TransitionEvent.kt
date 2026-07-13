package com.juul.krayon.transition

/**
 * Lifecycle events dispatched by a [Transition]. Register listeners with [on].
 *
 * See the analogous [d3 events](https://d3js.org/d3-transition/control-flow#transition_on).
 */
public enum class TransitionEvent {
    /** Dispatched when the transition starts, immediately before its tweens are initialized. */
    Start,

    /** Dispatched when the transition completes. */
    End,

    /** Dispatched when a running transition is interrupted by a newer transition of the same name. */
    Interrupt,

    /** Dispatched when a scheduled-but-not-yet-started transition is cancelled. */
    Cancel,
}
