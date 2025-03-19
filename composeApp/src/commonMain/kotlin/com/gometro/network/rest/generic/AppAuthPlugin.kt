package com.gometro.network.rest.generic

import com.gometro.network.config.NetworkConstants
import com.gometro.network.rest.generic.ChaloAuthPluginHelper.prepareSecureHttpRequest
import com.gometro.network.rest.generic.ChaloAuthPluginHelper.retryLimitReached
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Custom plugin which handles following functions -
 * 1. Adds headers like accessToken, userId etc
 * 2. If 401 received, attempts token refresh and retries api call with new token
 */
internal val GometroAuthPlugin = createClientPlugin(
    name = "GometroAuthPlugin",
    createConfiguration = {
        AppAuthConfig()
    },
) {
    val authConfig = this.pluginConfig
    on(Send) { request ->
        if (!request.headers.contains(NetworkConstants.Header.SECURE_API_HEADERS)) {
            // auth token not required
            return@on proceed(request)
        }

        val authHandler = authConfig.authHandler ?: return@on proceed(request)

        request.prepareSecureHttpRequest(authHandler::getAuthSecureData)
        // initial call with stored token
        var call = proceed(request)

        while (call.response.status == HttpStatusCode.Unauthorized) {
            when {
                call.response retryLimitReached  authHandler.retryLimitCount -> {
                    // token refresh limit reached, return the last result received and inform
                    // caller to take special action (ie. logout user)
                    authHandler.onRetryLimitExceeded.invoke(call.request.url.toString())
                    return@on call
                }
                !authHandler.tokenRefreshSuccessful(call.response) -> {
                    // token refresh failed
                    return@on call
                }
                else -> {
                    // retry request
                    request.prepareSecureHttpRequest(authHandler::getAuthSecureData)
                    call = proceed(request)
                }
            }
        }

        call
    }
}

internal class AppAuthConfig {

    var authHandler: AppAuthConfigHandler? = null
        private set

    fun setupAuthHandler(block: () -> AppAuthConfigHandler) {
        authHandler = block()
    }
}

internal class AppAuthConfigHandler(
    private val provideAuthSecureData: suspend () -> ChaloAuthSecureData,
    private val refreshTokens: suspend () -> Boolean,
    val onRetryLimitExceeded: suspend (url: String) -> Unit = {},
    val retryLimitCount: Int = NetworkConstants.Config.AUTHENTICATION_RETRY_LIMIT,
) {
    private val mutex by lazy { Mutex() }

    suspend fun getAuthSecureData(): ChaloAuthSecureData = provideAuthSecureData.invoke()

    suspend fun tokenRefreshSuccessful(response: HttpResponse): Boolean {
        mutex.withLock {
            val accessTokenUsedBefore = response.request.headers[NetworkConstants.Header.ACCESS_TOKEN]
            val accessTokenCurrentlyStored = getAuthSecureData().accessToken

            if (accessTokenUsedBefore != accessTokenCurrentlyStored) {
                return true
            } else {
                return refreshTokens.invoke()
            }
        }
    }

    internal data class ChaloAuthSecureData(
        val userId: String,
        val authType: String,
        val accessToken: String
    )

}

private object ChaloAuthPluginHelper {
    val retryAttemptCountAttributeKey = AttributeKey<Int>("retryAttemptCount")

    suspend fun HttpRequestBuilder.prepareSecureHttpRequest(
        getAuthSecureData: suspend () -> AppAuthConfigHandler.ChaloAuthSecureData,
    ) {
        val secureData = getAuthSecureData.invoke()
        this.updateRetryAttemptCountForRequest()
        this.headers {
            this.remove(NetworkConstants.Header.SECURE_API_HEADERS)
            this[NetworkConstants.Header.USER_ID] = secureData.userId
            this[NetworkConstants.Header.AUTH_TYPE] = secureData.authType
            this[NetworkConstants.Header.ACCESS_TOKEN] = secureData.accessToken
        }
    }

    infix fun HttpResponse.retryLimitReached(retryLimitCount: Int): Boolean {
        return getRetryAttemptCountForResponse(this) >= retryLimitCount
    }

    /**
     * Returns retry attempt count added in request attributes or null if not present
     */
    fun getRetryAttemptCountForResponse(response: HttpResponse): Int {
        return response.request.attributes.getOrNull(retryAttemptCountAttributeKey) ?: 0
    }

    /**
     * Updates retry attempt count by 1 or set to 0 if not present already in request attributes
     */
    fun HttpRequestBuilder.updateRetryAttemptCountForRequest() {
        val existingCount = this.attributes.getOrNull(retryAttemptCountAttributeKey)
        val updatedCount = existingCount?.plus(1) ?: 0
        this.attributes.put(retryAttemptCountAttributeKey, updatedCount)
    }
}
