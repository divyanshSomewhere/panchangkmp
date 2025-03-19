package com.gometro.login.data.repository


import com.gometro.userprofile.data.models.UserProfileAppModel
import com.gometro.userprofile.data.models.toUserProfileLocalInfoModel
import com.gometro.login.data.exceptions.LoginVerificationFailedException
import com.gometro.login.data.exceptions.ProfileAndTokensExceptions
import com.gometro.login.data.exceptions.TruecallerUidFetchFailedException
import com.gometro.login.data.local.LoginLocalDataSource
import com.gometro.login.data.mappers.toLoginApiRequestModel
import com.gometro.login.data.mappers.toPostLoginProfileAndTokensAppModel
import com.gometro.login.data.mappers.toRefreshTokenResponseAppModel
import com.gometro.login.data.models.app.LoginModeAppModel
import com.gometro.login.data.models.app.PostLoginProfileAndTokensAppModel
import com.gometro.login.data.models.app.RefreshTokenResponseAppModel
import com.gometro.login.data.models.request.LogoutUserRequestApiModel
import com.gometro.login.data.models.request.RefreshTokensRequestApiModel
import com.gometro.login.data.models.request.SendOtpRequestModel
import com.gometro.login.data.models.response.SendOtpResponseModel
import com.gometro.login.data.remote.LoginRemoteDataSource
import com.gometro.network.exception.ApiCallLocalNetworkException
import com.gometro.network.exception.NetworkSuccessResponseParseException
import kotlin.coroutines.cancellation.CancellationException

class LoginRepositoryImpl(
    private val remoteDataSource: LoginRemoteDataSource,
    private val localDataSource: LoginLocalDataSource
) : LoginRepository {

    @Throws(
        TruecallerUidFetchFailedException::class,
        ApiCallLocalNetworkException::class,
        NetworkSuccessResponseParseException::class,
        CancellationException::class
    )


    override suspend fun sendOtpForPhoneAuth(
        phoneNumber: String,
        countryCode: String,
        previousRefNoToResendOtp: String?,
        templateId: String
    ): SendOtpResponseModel {
        val requestBody = SendOtpRequestModel(
            mobileNumber = phoneNumber,
            countryCode = "+91",
            templateId = templateId,
            refNo = previousRefNoToResendOtp
        )
        return remoteDataSource.sendOtpForPhoneAuth(
            requestBody = requestBody,
            isResendOtpCall = previousRefNoToResendOtp != null
        )
    }

    @Throws(
        LoginVerificationFailedException::class,
        ApiCallLocalNetworkException::class,
        NetworkSuccessResponseParseException::class,
        ProfileAndTokensExceptions::class,
        CancellationException::class
    )
    override suspend fun verifyLoginSuccessOnServerAndGetTokens(
        deviceId: String,
        loginModeAppModel: LoginModeAppModel
    ): PostLoginProfileAndTokensAppModel {
        val loginRequestApiModel = loginModeAppModel.toLoginApiRequestModel(deviceId)
        return remoteDataSource
            .verifyLoginSuccessOnServerAndGetTokens(loginRequestApiModel)
            .toPostLoginProfileAndTokensAppModel()
    }

    override suspend fun makeLogoutUserCall(deviceId: String): Boolean {
        val request = LogoutUserRequestApiModel(
            accessToken = getAccessToken() ?: "",
            refreshToken = getRefreshToken() ?: "",
            userId = getUserId() ?: "",
            deviceId = deviceId
        )

        return remoteDataSource.makeLogoutUserCall(request)
    }

    override suspend fun storeTokensPostLogin(
        accessToken: String,
        refreshToken: String,
        expiryTime: Long?,
        delta: Long?
    ) {
        localDataSource.storeAuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiryTime = expiryTime,
            delta = delta
        )
    }

    override suspend fun storeUserProfileDetails(userProfileAppModel: UserProfileAppModel) {
        localDataSource.storeUserProfileDetails(userProfileAppModel.toUserProfileLocalInfoModel())
    }

    override suspend fun getUserId(): String? {
        return localDataSource.getUserId()
    }

    override suspend fun refreshAuthTokens(
        refreshToken: String,
        userId: String,
        deviceId: String
    ): RefreshTokenResponseAppModel {
        val refreshTokenRequestModel = RefreshTokensRequestApiModel(refreshToken, userId, deviceId)
        return remoteDataSource.refreshAuthTokens(refreshTokenRequestModel)
            .toRefreshTokenResponseAppModel()
    }

    override suspend fun getAccessToken(): String? {
        return localDataSource.getAccessToken()
    }

    override suspend fun getRefreshToken(): String? {
        return localDataSource.getRefreshToken()
    }

    override suspend fun getAccessTokenExpiry(): Long {
        return localDataSource.getAccessTokenExpiryTime()
    }

    override suspend fun getAccessTokenDelta(): Long {
        return localDataSource.getAccessTokenDelta()
    }

    override suspend fun setUserLoginFirstTime(isUserLoginFirstTime: Boolean) {
        localDataSource.setIsUserLoginFirstTime(isUserLoginFirstTime)
    }

    override suspend fun isUserLoginFirstTime(): Boolean {
        return localDataSource.isUserLoginFirstTime()
    }
}
