package com.juul.krayon.core

import android.app.Application
import android.content.Context

internal lateinit var application: Application

@InternalKrayonApi
public val Krayon.context: Context
    get() = application
