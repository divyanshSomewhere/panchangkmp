package com.gometro.network

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build

// this required ACCESS_NETWORK_STATE permission which is already granted during install
// suppressing this just for compile error
@SuppressLint("MissingPermission")
class KConnectivityManagerAndroid(
    private val connectivityManager: ConnectivityManager
) : KConnectivityManager, ConnectivityManager.NetworkCallback() {

    private var callback: ((NetworkConnectionType) -> Unit)? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(this)
        } else {
            connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), this)
        }
    }

    override fun getActiveNetworkConnectionState(): NetworkConnectionType {
        return connectivityManager.getActiveNetworkConnectionState()
    }

    override fun registerCallback(callback: (NetworkConnectionType) -> Unit) {
        this.callback = callback
    }

    override fun unregisterCallback() {
        this.callback = null
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        callback?.invoke(NetworkConnectionType.CONNECTED)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        callback?.invoke(NetworkConnectionType.DISCONNECTED)
    }
}