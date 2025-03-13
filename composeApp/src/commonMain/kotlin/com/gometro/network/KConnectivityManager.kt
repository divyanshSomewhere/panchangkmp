package com.gometro.network

interface KConnectivityManager {

    fun getActiveNetworkConnectionState(): NetworkConnectionType

    fun registerCallback(callback: (NetworkConnectionType) -> Unit)

    fun unregisterCallback()
}