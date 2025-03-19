package com.gometro.network

import com.gometro.network.config.NetworkConstants

interface AuthSecurityManager {

    suspend fun getUserId(): String

    suspend fun getAccessToken(): String

    /**
     * Refresh access tokens and return true if refreshed successfully
     */
    suspend fun refreshToken(): Boolean

    suspend fun onRetryLimitExceeded(url: String)

    val authType: String get() = NetworkConstants.General.AUTH_TYPE_ACCESS_TOKEN

    val retryLimitCount: Int get() = NetworkConstants.Config.AUTHENTICATION_RETRY_LIMIT

}
