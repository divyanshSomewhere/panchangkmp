package com.gometro.network

interface CommonHeaderProvider {
    fun getAppVersion(): String
    fun getDeviceId(): String
    fun getXType(): String
    fun getSource(): String
}
