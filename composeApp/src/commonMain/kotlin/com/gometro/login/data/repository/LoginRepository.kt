package com.gometro.login.data.repository

import com.gometro.userprofile.data.models.UserProfileAppModel
import com.gometro.login.data.exceptions.LoginVerificationFailedException
import com.gometro.login.data.exceptions.ProfileAndTokensExceptions
import com.gometro.login.data.exceptions.TruecallerUidFetchFailedException
import com.gometro.login.data.models.app.LoginModeAppModel
import com.gometro.login.data.models.app.PostLoginProfileAndTokensAppModel
import com.gometro.login.data.models.app.RefreshTokenResponseAppModel
import com.gometro.login.data.models.response.SendOtpResponseModel
import com.gometro.network.exception.ApiCallLocalNetworkException
import com.gometro.network.exception.NetworkSuccessResponseParseException
import kotlin.coroutines.cancellation.CancellationException

interface LoginRepository {

    @Throws(
        TruecallerUidFetchFailedException::class,
        ApiCallLocalNetworkException::class,
        NetworkSuccessResponseParseException::class,
        CancellationException::class
    )
//    suspend fun generateUidForTruecaller(): TruecallerUidResponseModel

    suspend fun sendOtpForPhoneAuth(
        phoneNumber: String,
        countryCode: String,
        previousRefNoToResendOtp: String?,
        templateId: String
    ): SendOtpResponseModel

    @Throws(
        LoginVerificationFailedException::class,
        ApiCallLocalNetworkException::class,
        NetworkSuccessResponseParseException::class,
        ProfileAndTokensExceptions::class,
        CancellationException::class
    )
    suspend fun verifyLoginSuccessOnServerAndGetTokens(deviceId: String, loginModeAppModel: LoginModeAppModel): PostLoginProfileAndTokensAppModel

    suspend fun makeLogoutUserCall(deviceId: String): Boolean

    suspend fun storeTokensPostLogin(
        accessToken: String,
        refreshToken: String,
        expiryTime: Long?,
        delta: Long?
    )

    suspend fun storeUserProfileDetails(
        userProfileAppModel: UserProfileAppModel
    )

    suspend fun getUserId(): String?

    suspend fun refreshAuthTokens(
        refreshToken: String,
        userId: String,
        deviceId: String
    ): RefreshTokenResponseAppModel

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun getAccessTokenExpiry(): Long

    suspend fun getAccessTokenDelta(): Long

    suspend fun setUserLoginFirstTime(isUserLoginFirstTime: Boolean)
    suspend fun isUserLoginFirstTime(): Boolean
}
