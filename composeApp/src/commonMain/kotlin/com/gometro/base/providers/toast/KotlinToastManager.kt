package com.gometro.base.providers.toast

import co.touchlab.skie.configuration.annotations.EnumInterop
import co.touchlab.skie.configuration.annotations.FlowInterop
import com.gometro.base.providers.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface KotlinToastManager {
    @FlowInterop.Enabled
    val msgStream: SharedFlow<KotlinToastData>

    fun postToastRequest(request: KotlinToastData)
}

class KotlinToastManagerImpl(
    coroutineContextProvider: CoroutineContextProvider
) : KotlinToastManager {

    private val _msgStream = MutableSharedFlow<KotlinToastData>()
    @FlowInterop.Enabled
    override val msgStream: SharedFlow<KotlinToastData> = _msgStream.asSharedFlow()

    private val scope = CoroutineScope(coroutineContextProvider.main)

    override fun postToastRequest(request: KotlinToastData) {
        scope.launch {
            _msgStream.emit(request)
        }
    }
}

data class KotlinToastData(
    val message: String,
    val length: ToastLength = ToastLength.LONG
)

@EnumInterop.Enabled
enum class ToastLength(val duration: Double) {
    SHORT(2.0),
    LONG(3.5)
}

fun ToastLength.toContextToastLength(): Int {
    return when (this) {
        ToastLength.SHORT -> 0
        ToastLength.LONG -> 1
    }
}