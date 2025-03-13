package com.gometro.network

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
fun ConnectivityManager.getActiveNetworkConnectionState(): NetworkConnectionType {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getNetworkCapabilities(activeNetwork)?.let {
                if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                ) {
                    NetworkConnectionType.CONNECTED
                } else {
                    NetworkConnectionType.DISCONNECTED
                }
            } ?: NetworkConnectionType.DISCONNECTED
        } else {
            if (activeNetworkInfo?.isConnected == true) {
                NetworkConnectionType.CONNECTED
            } else {
                NetworkConnectionType.DISCONNECTED
            }
        }
    } catch (e: SecurityException) {
        NetworkConnectionType.UNKNOWN
    }
}
