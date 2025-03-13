package com.gometro.network

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class NetworkConnectionType {
    UNKNOWN,
    DISCONNECTED,
    CONNECTED
}
