package com.gometro.buildconfig

import co.touchlab.skie.configuration.annotations.EnumInterop
import co.touchlab.skie.configuration.annotations.SealedInterop


interface GometroBuildConfig {
    val environment: Environment
    val platform: Platform
    val versionCode: Int
    val isDebugBuild: Boolean
    val deviceModel: String
    val osVersion: Int
}

@EnumInterop.Enabled
enum class Environment {
    PRODUCTION, DEVELOPMENT, STAGING;

    companion object {
        fun safeValueOf(str: String): Environment? {
            return try {
                Environment.valueOf(str)
            } catch (e: Exception) {
                null
            }
        }
    }
}

@SealedInterop.Enabled
enum class ProductFlavor {
    PRIMARY, BETA, ALPHA;

    companion object {
        fun safeValueOf(str: String): ProductFlavor? {
            return ProductFlavor.entries.find { it.name.equals(str, ignoreCase = true) }
        }
    }
}

@SealedInterop.Enabled
enum class Platform {
    ANDROID, IOS
}

fun Environment.toChaloBaseUrlEnvPrefix(): String {
    return when (this) {
        Environment.PRODUCTION -> "production"
        Environment.DEVELOPMENT -> "dev"
        Environment.STAGING -> "preprod"
    }
}

fun Environment.toChaloConfigurationPrefix(shouldUseDevForPreprod: Boolean): String {
    return when (this) {
        Environment.PRODUCTION -> "production"
        Environment.STAGING -> if (shouldUseDevForPreprod) "dev" else "preprod"
        Environment.DEVELOPMENT -> "dev"
    }
}

fun Environment.toGeoSpatialApiPrefix(): String {
    return when (this) {
        Environment.PRODUCTION -> "geoquery"
        Environment.DEVELOPMENT -> "devgeoquery"
        Environment.STAGING -> "preprod-geoquery"
    }
}

fun Environment.toCashApiPrefix(): String {
    return when (this) {
        Environment.PRODUCTION -> "cash"
        Environment.DEVELOPMENT -> "devcash"
        Environment.STAGING -> "preprod-cash"
    }
}

fun Environment.toFirebasePollingPrefix(): String {
    return when (this) {
        Environment.PRODUCTION,
        Environment.DEVELOPMENT -> "production"
        Environment.STAGING -> "preprod"
    }
}
