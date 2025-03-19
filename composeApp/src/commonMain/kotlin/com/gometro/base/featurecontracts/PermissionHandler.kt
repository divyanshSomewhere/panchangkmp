package com.gometro.base.featurecontracts

import com.gometro.base.permissions.models.AppPermission
import com.gometro.base.permissions.models.PermissionState
import kotlinx.coroutines.flow.Flow

interface PermissionHandler {

    suspend fun checkPermission(permission: AppPermission): PermissionState

    fun requestPermission(permission: AppPermission): Flow<PermissionState>

    fun requestMultiplePermission(permissions: List<AppPermission>): Flow<List<Pair<AppPermission, PermissionState>>>

}
