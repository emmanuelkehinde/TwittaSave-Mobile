package com.emmanuelkehinde.shared.coroutines

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class AndroidCoroutineContextProvider : CoroutineContextProvider {
    override val main: CoroutineContext
        get() = Dispatchers.Main
    override val io: CoroutineContext
        get() = Dispatchers.IO
}
