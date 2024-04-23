package com.juul.krayon.kanvas

import platform.CoreFoundation.CFAttributedStringCreate
import platform.CoreFoundation.CFAttributedStringGetLength
import platform.CoreFoundation.CFAttributedStringRef
import platform.CoreFoundation.CFAttributedStringSetAttribute
import platform.CoreFoundation.CFRangeMake
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFStringEncodingUTF8

internal inline fun <T> withCFString(
    string: String,
    crossinline actions: (CFStringRef) -> T,
): T {
    val cfString = CFStringCreateWithCString(kCFAllocatorDefault, string, kCFStringEncodingUTF8)!!
    try {
        return actions(cfString)
    } finally {
        CFRelease(cfString)
    }
}

internal inline fun <T> withCFAttributedString(
    string: String,
    crossinline actions: (CFAttributedStringRef) -> T,
): T {
    val attributedString = withCFString(string) { cfString ->
        CFAttributedStringCreate(kCFAllocatorDefault, cfString, null)!!
    }
    try {
        return actions(attributedString)
    } finally {
        CFRelease(attributedString)
    }
}

internal operator fun CFAttributedStringRef.set(
    key: CFStringRef?,
    range: IntRange = fullRange,
    value: CFTypeRef?,
) {
    // TODO: Double check if the off-by-1 is more properly resolved by changing the `until` to `..` below
    val cfRange = CFRangeMake(range.first.toLong(), (range.last - range.first + 1).toLong())
    CFAttributedStringSetAttribute(this, cfRange, key, value)
}

private val CFAttributedStringRef.fullRange: IntRange
    get() = 0 until CFAttributedStringGetLength(this).toInt()
