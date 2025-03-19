package com.gometro.base.permissions.models

enum class PermissionState {
    NOT_DETERMINED,
    GRANTED,
    DENIED,
    REQUIRES_EXPLANATION,
    UNKNOWN
}

fun PermissionState.isGranted(): Boolean {
    return this == PermissionState.GRANTED
}

enum class LocationAccuracyState {
    REDUCED,
    FULL,
    UNKNOWN
}