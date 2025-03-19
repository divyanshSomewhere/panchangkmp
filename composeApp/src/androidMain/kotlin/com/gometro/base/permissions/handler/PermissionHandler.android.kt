package com.gometro.base.permissions.handler

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gometro.base.featurecontracts.CoroutineContextProvider
import com.gometro.base.featurecontracts.PermissionHandler
import com.gometro.base.permissions.models.AppPermission
import com.gometro.base.permissions.models.PermissionState
import com.gometro.base.permissions.models.isGranted
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class PermissionHandlerAndroid(
    private val activity: AppCompatActivity,
    private val coroutineContextProvider: CoroutineContextProvider
) : PermissionHandler {

    private val permissionStatesInfo = MutableStateFlow<List<PermissionUpdateInfo>>(listOf())

    private val permissionResultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionStateMap ->
            val appPermissionMap = permissionStateMap.toAppPermissionAndState()
            val permissionStateReceivedAt = System.currentTimeMillis()

            permissionStatesInfo.update { permissionStateList ->
                val existingPermissionStateMap = permissionStateList.associateBy { it.permission }.toMutableMap()
                // update existing permissions state with received data
                appPermissionMap.forEach { entry ->
                    existingPermissionStateMap[entry.key] = PermissionUpdateInfo(
                        permission = entry.key,
                        permissionState = entry.value,
                        stateUpdatedAt = permissionStateReceivedAt
                    )
                }
                existingPermissionStateMap.values.toList()
            }
        }

    override suspend fun checkPermission(permission: AppPermission): PermissionState {
        return withContext(coroutineContextProvider.main) {
            when(permission) {
                AppPermission.POST_NOTIFICATIONS -> {
                    checkNotificationPermission()
                }
                AppPermission.CAMERA,
                AppPermission.COARSE_LOCATION,
                AppPermission.FINE_LOCATION -> {
                    checkBasicPermission(permission)
                }
            }
        }
    }

    private fun checkNotificationPermission(): PermissionState {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return checkBasicPermission(AppPermission.POST_NOTIFICATIONS)
        }

        return PermissionState.GRANTED
    }

    /**
     * If a permission requires build version check, that should happen before calling this method
     */
    private fun checkBasicPermission(permission: AppPermission): PermissionState {
        val androidPermission = permission.toAndroidPermission()

        return when {
            isPermissionGranted(androidPermission) -> PermissionState.GRANTED
            shouldShowRationale(androidPermission) -> PermissionState.REQUIRES_EXPLANATION
            else -> PermissionState.DENIED
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun requestPermission(permission: AppPermission): Flow<PermissionState> {
        return flow {
            emit(checkPermission(permission))
        }.flatMapLatest { permissionState ->
            if (permissionState.isGranted()) {
                flowOf(PermissionState.GRANTED)
            } else {
                val permissionRequestedAt = System.currentTimeMillis()

                // requesting permission
                permissionResultLauncher.launch(arrayOf(permission.toAndroidPermission()))

                permissionStatesInfo
                    .mapNotNull {
                        it.firstOrNull { info ->
                            // if updatedAt < requestedAt then that could be stale info
                            info.permission == permission && info.stateUpdatedAt >= permissionRequestedAt
                        }
                    }
                    .distinctUntilChanged()
                    .map { it.permissionState }
                    .distinctUntilChanged()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun requestMultiplePermission(permissions: List<AppPermission>): Flow<List<Pair<AppPermission, PermissionState>>> {
        val permissionsSet = permissions.toSet()
        val permissionRequestedAt = System.currentTimeMillis()

        return flow {
            val permissionsAlreadyGranted = permissionsSet.filter { checkPermission(it).isGranted() }.toSet()
            val permissionsToRequest = permissionsSet - permissionsAlreadyGranted

            emit((permissionsAlreadyGranted to permissionsToRequest))
        }.flatMapLatest { grantedAndToRequestPair ->
            if (grantedAndToRequestPair.second.isEmpty()) {
                flowOf(
                    permissionsSet.map { it to PermissionState.GRANTED }
                )
            } else {
                val permissionToRequestArray = grantedAndToRequestPair.second.map { it.toAndroidPermission() }.toTypedArray()
                permissionResultLauncher.launch(permissionToRequestArray)

                combine(
                    flow = permissionAlreadyGrantedFlow(grantedAndToRequestPair.first),
                    flow2 = permissionsToRequestFlow(
                        permissions = grantedAndToRequestPair.second,
                        requestedAtTimestamp = permissionRequestedAt
                    )
                ) { stateForAlreadyGranted, stateForRequestedAt ->
                    val finalList = stateForAlreadyGranted + stateForRequestedAt
                    finalList.map { it.permission to it.permissionState }
                }
            }
        }.distinctUntilChanged()
    }

    private fun permissionAlreadyGrantedFlow(
        permissions: Set<AppPermission>
    ): Flow<List<PermissionUpdateInfo>> {
        val currentTime = System.currentTimeMillis()
        val updateInfo = permissions.map {
            PermissionUpdateInfo(
                permission = it,
                permissionState = PermissionState.GRANTED,
                stateUpdatedAt = currentTime
            )
        }
        return flowOf(updateInfo)
    }

    private fun permissionsToRequestFlow(
        permissions: Set<AppPermission>,
        requestedAtTimestamp: Long
    ): Flow<List<PermissionUpdateInfo>> {
        return permissionStatesInfo
            .mapNotNull { existing ->
                val existingData = existing.filter { info ->
                    permissions.contains(info.permission) && info.stateUpdatedAt >= requestedAtTimestamp
                }

                // we have updated info for all the permissions that we requested
                val isValid = permissions == existingData.map { it.permission }.toSet()
                if (isValid) {
                    existingData
                } else {
                    null
                }
            }
            .distinctUntilChanged()
    }

    private fun Map<String, Boolean>.toAppPermissionAndState(): Map<AppPermission, PermissionState> {
        val result = mutableMapOf<AppPermission, PermissionState>()
        this.forEach { entry ->
            fromAndroidPermission(entry.key)?.let { appPermission ->
                result[appPermission] = when {
                    entry.value -> PermissionState.GRANTED
                    shouldShowRationale(appPermission.toAndroidPermission()) -> PermissionState.REQUIRES_EXPLANATION
                    else -> PermissionState.DENIED
                }
            }
        }

        return result
    }

    private fun isPermissionGranted(permissionName: String): Boolean {
        return ContextCompat.checkSelfPermission(
            /* context = */ activity,
            /* permission = */ permissionName
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowRationale(permissionName: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            /* activity = */ activity,
            /* permission = */ permissionName
        )
    }
}

private data class PermissionUpdateInfo(
    val permission: AppPermission,
    val permissionState: PermissionState,
    val stateUpdatedAt: Long
)

internal fun AppPermission.toAndroidPermission(): String {
    return when(this) {
        AppPermission.CAMERA -> Manifest.permission.CAMERA
        AppPermission.COARSE_LOCATION -> Manifest.permission.ACCESS_COARSE_LOCATION
        AppPermission.FINE_LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
        AppPermission.POST_NOTIFICATIONS -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                // this should not be called if not tiramisu or above, should be handled in handler
                "POST_NOTIFICATIONS"
            }
        }
    }
}

internal fun fromAndroidPermission(permissionName: String): AppPermission? {
    return when(permissionName) {
        Manifest.permission.CAMERA -> AppPermission.CAMERA
        Manifest.permission.ACCESS_COARSE_LOCATION -> AppPermission.COARSE_LOCATION
        Manifest.permission.ACCESS_FINE_LOCATION -> AppPermission.FINE_LOCATION
        Manifest.permission.POST_NOTIFICATIONS, "POST_NOTIFICATIONS" -> AppPermission.POST_NOTIFICATIONS
        else -> null
    }
}