package com.juul.krayon.element.view

import kotlinx.coroutines.flow.Flow

public expect class ElementViewAdapter<T>(
    dataSource: Flow<T>,
    updater: UpdateElement<T>,
)
