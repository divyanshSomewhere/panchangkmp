package com.gometro.base.providers

import com.gometro.buildconfig.AppBuildConfig
import com.gometro.buildconfig.toBaseUrlEnvPrefix
import com.gometro.network.AppUrlProvider

class AppUrlProviderImpl(
    buildConfig: AppBuildConfig,
): AppUrlProvider {

    private val environment = buildConfig.environment

    override fun getBaseUrl(): String {
        return BASE_URL_FORMAT.replace(
            SUB_DOMAIN,
            environment.toBaseUrlEnvPrefix()
        ).replace(
            DOMAIN,
            BASE_DOMAIN
        )
    }

    companion object {
        private const val BASE_DOMAIN = "gometro.com"
        private const val TAG = "URLHELPER"

        private const val SUB_DOMAIN = "env"
        private const val DOMAIN = "domain"

        private const val BASE_URL_FORMAT = "https://$SUB_DOMAIN.$DOMAIN"
    }

}