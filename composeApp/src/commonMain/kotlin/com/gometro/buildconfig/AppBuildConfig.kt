package com.gometro.buildconfig

import co.touchlab.skie.configuration.annotations.EnumInterop
import co.touchlab.skie.configuration.annotations.SealedInterop


interface AppBuildConfig {
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

fun Environment.toBaseUrlEnvPrefix(): String {
    return when (this) {
        Environment.PRODUCTION -> "production"
        Environment.DEVELOPMENT -> "dev"
        Environment.STAGING -> "preprod"
    }
}
