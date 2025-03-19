package com.gometro.login.data.remote

import app.chalo.login.data.models.request.LoginApiRequestModel
import com.gometro.login.data.exceptions.LoginVerificationFailedException
import com.gometro.login.data.exceptions.RefreshAuthTokensFailedException
import com.gometro.login.data.exceptions.SendOtpFailedException
import com.gometro.login.data.exceptions.TruecallerUidFetchFailedException
import com.gometro.login.data.models.request.LogoutUserRequestApiModel
import com.gometro.login.data.models.request.RefreshTokensRequestApiModel
import com.gometro.login.data.models.request.SendOtpRequestModel
import com.gometro.login.data.models.response.PostLoginAuthTokensResponseModel
import com.gometro.login.data.models.response.RefreshTokensResponseApiModel
import com.gometro.login.data.models.response.SendOtpResponseModel
import com.gometro.login.data.models.response.TruecallerUidResponseModel
import kotlin.coroutines.cancellation.CancellationException

interface LoginRemoteDataSource {

    @Throws(TruecallerUidFetchFailedException::class, CancellationException::class)
    suspend fun generateUidForTruecaller(): TruecallerUidResponseModel

    @Throws(SendOtpFailedException::class, CancellationException::class)
    suspend fun sendOtpForPhoneAuth(requestBody: SendOtpRequestModel, isResendOtpCall: Boolean): SendOtpResponseModel

    @Throws(LoginVerificationFailedException::class, CancellationException::class)
    suspend fun verifyLoginSuccessOnServerAndGetTokens(loginApiRequestModel: LoginApiRequestModel): PostLoginAuthTokensResponseModel

    @Throws(RefreshAuthTokensFailedException::class, CancellationException::class)
    suspend fun refreshAuthTokens(refreshTokensRequestApiModel: RefreshTokensRequestApiModel): RefreshTokensResponseApiModel

    suspend fun makeLogoutUserCall(request: LogoutUserRequestApiModel): Boolean
}
