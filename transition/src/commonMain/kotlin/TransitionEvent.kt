package com.juul.krayon.transition

/**
 * Lifecycle events dispatched by a [Transition]. Register listeners with [on].
 *
 * See the analogous [d3 events](https://d3js.org/d3-transition/control-flow#transition_on).
 */
public enum class TransitionEvent {
    /** Dispatched when the transition starts, immediately before its tweens are initialized. */
    Start,

    /** Dispatched when the transition completes normally. */
    End,

    /** Dispatched when a *running* transition is stopped early, either by [interrupt] or by a replacement of the same name. */
    Interrupt,

    /** Dispatched when a *pending* transition is stopped before starting, either by [interrupt] or by a replacement of the same name. */
    Cancel,
}
