package com.gometro.base.basefeatureimpl


import com.gometro.base.featurecontracts.BasicInfoContract
import com.gometro.base.featurecontracts.Device
import com.gometro.base.featurecontracts.SystemHelper
import com.gometro.base.permissions.handler.PermissionHandlerProvider
import com.gometro.base.permissions.models.AppPermission
import com.gometro.base.permissions.models.isGranted
import com.gometro.buildconfig.AppBuildConfig

class BasicInfoContractImpl(
    private val device: Device,
    private val systemHelper: SystemHelper,
    private val appBuildConfig: AppBuildConfig,
    private val permissionHandlerProvider: PermissionHandlerProvider
) : BasicInfoContract {

    override suspend fun hasPermissions(vararg permissions: AppPermission): Boolean {
        val permissionHandler = permissionHandlerProvider.permissionHandler ?: return false
        return permissions.toList().all { permissionHandler.checkPermission(it).isGranted() }
    }

    override fun getSystemTime(): Long {
        return systemHelper.currentTimeInMillis()
    }

    override fun isLiveDebug(): Boolean {
        return appBuildConfig.isDebugBuild
    }

    override fun getAppVersionCode(): Int {
        return appBuildConfig.versionCode
    }

    override fun getDeviceId(): String {
        return device.getDeviceId()
    }

    override val osVersion: Int = appBuildConfig.osVersion

    override val model: String = appBuildConfig.deviceModel
}