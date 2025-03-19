package com.gometro.login.data.exceptions

import com.gometro.login.data.models.response.UserProfileResponseModel


/**
 * Exceptions thrown on mapping post login response model to app models
 */
sealed class ProfileAndTokensExceptions(open val msg: String) : Exception(msg) {

    data class InvalidAccessToken(
        val tokenReceived: String?,
        override val msg: String
    ) : ProfileAndTokensExceptions(msg)

    data class InvalidRefreshToken(
        val tokenReceived: String?,
        override val msg: String
    ) : ProfileAndTokensExceptions(msg)

    data class InvalidProfileDetails(
        val profileReceived: UserProfileResponseModel?,
        override val msg: String
    ) : ProfileAndTokensExceptions(msg)
}
