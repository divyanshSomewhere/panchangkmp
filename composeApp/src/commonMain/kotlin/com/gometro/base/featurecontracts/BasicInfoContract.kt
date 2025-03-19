package com.gometro.base.featurecontracts

import com.gometro.base.permissions.models.AppPermission

interface BasicInfoContract {

    suspend fun hasPermissions(vararg permissions: AppPermission): Boolean

    fun getSystemTime(): Long

    fun isLiveDebug(): Boolean

    fun getAppVersionCode(): Int

    fun getDeviceId(): String


    val osVersion: Int
    val model: String
}