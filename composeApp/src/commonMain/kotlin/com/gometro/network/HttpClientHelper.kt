package com.gometro.network

import com.gometro.base.utils.CustomJsonParser
import com.gometro.network.config.NetworkConstants
import com.gometro.network.rest.generic.AppAuthConfigHandler
import com.gometro.network.rest.generic.GometroAuthPlugin
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.headers
import io.ktor.http.HeadersBuilder
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAndValueAbsent

object HttpClientHelper {

    internal fun getHttpClientDefinitionForPriority(
        httpClientEngine: HttpClientEngine,
        commonHeaderProvider: CommonHeaderProvider,
        authSecurityManager: AuthSecurityManager
    ): HttpClient {
        return HttpClient(httpClientEngine) {
            setupContentNegotiation()
            setupChaloAuth(authSecurityManager)
            setupDefaultRequest(commonHeaderProvider)
        }
    }

    private fun HttpClientConfig<*>.setupContentNegotiation() {
        install(ContentNegotiation) {
            json(CustomJsonParser.Json)
        }
    }

    private fun HttpClientConfig<*>.setupChaloAuth(
        authSecurityManager: AuthSecurityManager
    ) {
        install(GometroAuthPlugin) {
            setupAuthHandler {
                AppAuthConfigHandler(
                    provideAuthSecureData = {
                        AppAuthConfigHandler.ChaloAuthSecureData(
                            userId = authSecurityManager.getUserId(),
                            authType = authSecurityManager.authType,
                            accessToken = authSecurityManager.getAccessToken()
                        )
                    },
                    refreshTokens = authSecurityManager::refreshToken,
                    onRetryLimitExceeded = authSecurityManager::onRetryLimitExceeded,
                    retryLimitCount = authSecurityManager.retryLimitCount
                )
            }
        }
    }

    private fun HttpClientConfig<*>.setupDefaultRequest(
        commonHeaderProvider: CommonHeaderProvider
    ) {
        defaultRequest {
            this.headers { addCommonHeaders(commonHeaderProvider) }
        }
    }

    private fun HeadersBuilder.addCommonHeaders(commonHeaderProvider: CommonHeaderProvider) {
        this.appendIfNameAndValueAbsent(
            NetworkConstants.Header.KEY_CONTENT_TYPE,
            NetworkConstants.Header.CONTENT_TYPE_JSON
        )
        this.appendIfNameAndValueAbsent(
            NetworkConstants.Header.KEY_ACCEPT,
            NetworkConstants.Header.CONTENT_TYPE_JSON
        )
        this.appendIfNameAndValueAbsent(NetworkConstants.Header.SOURCE, commonHeaderProvider.getSource())
        this.appendIfNameAndValueAbsent(NetworkConstants.Header.DEVICE_ID, commonHeaderProvider.getDeviceId())
        this.appendIfNameAndValueAbsent(NetworkConstants.Header.APP_VERSION, commonHeaderProvider.getAppVersion())
        if (this.contains(NetworkConstants.Header.X_TYPE)) {
            this.remove(NetworkConstants.Header.X_TYPE)
            this.append(NetworkConstants.Header.X_TYPE, commonHeaderProvider.getXType())
        }
    }

}