package com.gometro.base.featurecontracts

interface SystemHelper {

    fun currentTimeInMillis(): Long

    fun elapsedRealTime(): Long

    fun openApplicationSettings()
}