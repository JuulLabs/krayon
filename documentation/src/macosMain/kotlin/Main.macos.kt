package com.juul.krayon.documentation

import androidx.compose.ui.window.Window
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import platform.AppKit.NSApplication
import platform.ApplicationServices.TransformProcessType
import platform.ApplicationServices.kCurrentProcess
import platform.ApplicationServices.kProcessTransformToForegroundApplication
import platform.darwin.ProcessSerialNumber

@OptIn(ExperimentalForeignApi::class)
fun main() {
    // > There is no support a packaging resources for Compose macOS target.
    // https://youtrack.jetbrains.com/issue/CMP-4813#focus=Comments-27-10103096.0-0
//    val title = runBlocking { getString(Res.string.app_title) }
    val title = "Krayon: Documentation"

    Window(title) { App() }
    val process = nativeHeap.alloc<ProcessSerialNumber>()
    process.lowLongOfPSN = kCurrentProcess
    TransformProcessType(process.ptr, kProcessTransformToForegroundApplication)
    nativeHeap.free(process.rawPtr)
    NSApplication.sharedApplication.activate()
    NSApplication.sharedApplication.run()
}
