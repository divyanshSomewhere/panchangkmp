package com.gometro.network

import com.gometro.logger.AppLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NetworkStateManagerImpl(
    connectivityManager: KConnectivityManager
) : NetworkStateManager {

    private val _networkState = MutableStateFlow(
        connectivityManager.getActiveNetworkConnectionState()
    ).apply {
        AppLog.debug("NetworkStateManager", "Network state initialized to ${value.name}")
    }

    override val networkState: StateFlow<NetworkConnectionType>
        get() = _networkState.asStateFlow()

    init {
        connectivityManager.registerCallback {
            setState(it)
        }
    }

    private fun setState(state: NetworkConnectionType) {
        if (networkState.value != state) {
            AppLog.debug("NetworkStateManager", "Network state changed to \"${state.name}\"")
        }
        _networkState.update { state }
    }
}
