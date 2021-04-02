package com.juul.krayon.color

internal const val MASK_COMPONENT = 0xFF
internal const val MASK_RGB = 0xFFFFFF

internal const val SHIFT_ALPHA = 24
internal const val SHIFT_RED = 16
internal const val SHIFT_GREEN = 8
internal const val SHIFT_BLUE = 0

internal const val HEX_BASE = 16

internal const val COMPONENT_MIN = 0x00
internal const val COMPONENT_MAX = 0xFF
internal val COMPONENT_RANGE = COMPONENT_MIN..COMPONENT_MAX

internal const val FLOAT_COMPONENT_MIN = 0f
internal const val FLOAT_COMPONENT_MAX = 1f
internal val FLOAT_COMPONENT_RANGE = FLOAT_COMPONENT_MIN..FLOAT_COMPONENT_MAX
