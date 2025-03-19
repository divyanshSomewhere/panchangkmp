package com.gometro.base.featurecontracts

import co.touchlab.skie.configuration.annotations.EnumInterop
import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.SharedFlow

interface KotlinToastManager {
    @FlowInterop.Enabled
    val msgStream: SharedFlow<KotlinToastData>

    fun postToastRequest(request: KotlinToastData)
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