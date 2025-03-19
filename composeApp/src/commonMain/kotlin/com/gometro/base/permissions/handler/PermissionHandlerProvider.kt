package com.gometro.base.permissions.handler

import com.gometro.base.featurecontracts.PermissionHandler

interface PermissionHandlerProvider {

    val permissionHandler: PermissionHandler?

}

interface PermissionHandlerSetter : PermissionHandlerProvider {

    fun setPermissionHandler(permissionHandler: PermissionHandler)
    fun removePermissionHandler()

}

class PermissionHandlerProviderImpl : PermissionHandlerSetter {
    private var _permissionHandler: PermissionHandler? = null
    override val permissionHandler: PermissionHandler? get() = _permissionHandler

    override fun setPermissionHandler(permissionHandler: PermissionHandler) {
        _permissionHandler = permissionHandler
    }

    override fun removePermissionHandler() {
        _permissionHandler = null
    }
}