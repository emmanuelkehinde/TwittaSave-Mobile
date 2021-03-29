package com.emmanuelkehinde.shared.coroutines

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal class IosCoroutineContextProvider: CoroutineContextProvider {
    override val main: CoroutineContext
        get() = Dispatchers.Main
    override val io: CoroutineContext
        get() = Dispatchers.Default
}