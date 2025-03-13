package com.gometro.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkStateManager {
    /** [NetworkStates] */
    val networkState: StateFlow<NetworkConnectionType>
}

fun NetworkStateManager.isConnected() = networkState.value == NetworkConnectionType.CONNECTED
