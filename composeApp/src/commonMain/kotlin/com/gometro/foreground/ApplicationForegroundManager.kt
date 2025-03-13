package com.gometro.foreground

interface ApplicationForegroundManager {

    val isInForeground: Boolean

    fun addListener(listener: ForegroundListener)

    fun removeListener(listener: ForegroundListener)

}

interface ForegroundListener {

    // id is for uniquely identifying the listener instance. this is needed specifically for ios
    // where in order to remove listener from an array we need something unique and normal instance
    // equality was not working
    val id: String

    fun onBecameBackground()

    fun onBecameForeground()

}