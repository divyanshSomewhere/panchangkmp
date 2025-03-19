package com.gometro.base.featurecontracts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

interface CoroutineContextProvider {
    val main: CoroutineContext
    val io: CoroutineContext
    val default: CoroutineContext
}

class CoroutineContextProviderImpl : CoroutineContextProvider {
    override val main: CoroutineContext get() = Dispatchers.Main
    override val io: CoroutineContext get() = Dispatchers.IO
    override val default: CoroutineContext get() = Dispatchers.Default
}