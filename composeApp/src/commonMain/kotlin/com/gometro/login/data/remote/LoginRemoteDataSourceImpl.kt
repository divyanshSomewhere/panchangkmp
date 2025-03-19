package com.gometro.login.data.remote

import app.chalo.login.data.models.request.LoginApiRequestModel
import com.gometro.login.data.LoginApiCallsType
import com.gometro.login.data.LoginRemoteExceptionAndErrorHandler
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
import com.gometro.network.NetworkManager
import com.gometro.network.mapper.GenericNetworkExceptionMapper
import com.gometro.network.rest.request.HttpRequestType
import com.gometro.network.rest.response.NetworkResponse
import kotlin.coroutines.cancellation.CancellationException

class LoginRemoteDataSourceImpl(
    private val networkManager: NetworkManager,
    private val genericNetworkExceptionMapper: GenericNetworkExceptionMapper,
    private val loginRemoteExceptionAndErrorHandler: LoginRemoteExceptionAndErrorHandler
) : LoginRemoteDataSource {

    @Throws(TruecallerUidFetchFailedException::class, CancellationException::class)
    override suspend fun generateUidForTruecaller(): TruecallerUidResponseModel {
        val networkResponse = networkManager.getStandardNetworkRequestBuilder().subUrl(GENERATE_UID_SUB_URL)
            .httpMethod(HttpRequestType.GET)
            .addSecureApiHeaders()
            .build()
            .processSync()

        return if (networkResponse.isSuccess) {
            networkResponse.getSuccessResponseOrThrowParseException()
        } else {
            throw handleFailureCasesForLoginCallsType(networkResponse, LoginApiCallsType.GENERATE_UID)
        }
    }

    @Throws(SendOtpFailedException::class, CancellationException::class)
    override suspend fun sendOtpForPhoneAuth(
        requestBody: SendOtpRequestModel,
        isResendOtpCall: Boolean
    ): SendOtpResponseModel {
        val subUrl = if (isResendOtpCall) RESEND_OTP_SUB_URL else SEND_OTP_SUB_URL

        val networkResponse = networkManager.getStandardNetworkRequestBuilder().subUrl(subUrl)
            .body(requestBody)
            .addHeader(USER_ID, getUserIdHeaderValueForLoginApi(requestBody.mobileNumber, requestBody.countryCode)) // do not interpret this userId as actual userId, this is only for rate-limiting purpose
            .httpMethod(HttpRequestType.POST)
            .build()
            .processSync()

        return if (networkResponse.isSuccess) {
            networkResponse.getSuccessResponseOrThrowParseException()
        } else {
            throw handleFailureCasesForLoginCallsType(networkResponse, LoginApiCallsType.SEND_OTP)
        }
    }

    @Throws(LoginVerificationFailedException::class, CancellationException::class)
    override suspend fun verifyLoginSuccessOnServerAndGetTokens(loginApiRequestModel: LoginApiRequestModel): PostLoginAuthTokensResponseModel {
        val userIdForHeader = when (loginApiRequestModel) {
            is LoginApiRequestModel.PhoneAuthLoginApiRequestModel -> {
                getUserIdHeaderValueForLoginApi(loginApiRequestModel.mobileNumber, loginApiRequestModel.countryCode)
            }
            is LoginApiRequestModel.TruecallerLoginApiRequestModel -> {
                getUserIdHeaderValueForLoginApi(loginApiRequestModel.mobileNumber, loginApiRequestModel.countryCode)
            }
        }

        val networkResponse = networkManager.getStandardNetworkRequestBuilder().subUrl(VERIFY_LOGIN_SUB_URL)
            .httpMethod(HttpRequestType.POST)
            .addHeader(USER_ID, userIdForHeader) // do not interpret this userId as actual userId, this is only for rate-limiting purpose
            .body(loginApiRequestModel)
            .build()
            .processSync()

        return if (networkResponse.isSuccess) {
            networkResponse.getSuccessResponseOrThrowParseException()
        } else {
            throw genericNetworkExceptionMapper.invoke(networkResponse) {
                LoginVerificationFailedException(it, it?.message)
            }
        }
    }

    @Throws(RefreshAuthTokensFailedException::class, CancellationException::class)
    override suspend fun refreshAuthTokens(refreshTokensRequestApiModel: RefreshTokensRequestApiModel): RefreshTokensResponseApiModel {
        val networkResponse = networkManager.getStandardNetworkRequestBuilder().subUrl(REFRESH_TOKEN_SUB_URL)
            .httpMethod(HttpRequestType.POST)
            .body(refreshTokensRequestApiModel)
            .build()
            .processSync()

        return if (networkResponse.isSuccess) {
            networkResponse.getSuccessResponseOrThrowParseException()
        } else {
            throw handleFailureCasesForLoginCallsType(networkResponse, LoginApiCallsType.REFRESH_TOKENS)
        }
    }


    override suspend fun makeLogoutUserCall(request: LogoutUserRequestApiModel): Boolean {
        val response = networkManager.getStandardNetworkRequestBuilder()
            .subUrl(LOGOUT_USER_SUB_URL)
            .httpMethod(HttpRequestType.POST)
            .body(request)
            .build()
            .processSync()

        return response.isSuccess
    }

    /**
     * error responses for login related apis are old ones, hence they do not follow the new error
     * response structure, so passing unknownErrorToGenericChaloErrorResponse lambda
     *
     * example of error response for these apis -
     * ```
     * {
     * "statusCode": 500,
     * "error": "Internal Server Error",
     * "message": "Cannot read property 'match' of undefined"
     * }
     * ```
     */
    private fun handleFailureCasesForLoginCallsType(
        networkResponse: NetworkResponse,
        apiCallsType: LoginApiCallsType
    ): Exception {
        return genericNetworkExceptionMapper.invoke(
            networkResponse = networkResponse,
            unknownErrorToGenericChaloErrorResponse = { responseString ->
                loginRemoteExceptionAndErrorHandler
                    .createGenericChaloErrorResponseFromJsonString(
                        jsonString = responseString,
                        apiCallType = apiCallsType
                    )
            },
            createException = {
                loginRemoteExceptionAndErrorHandler
                    .createLoginCallsException(
                        genericChaloErrorResponse = it,
                        apiCallType = apiCallsType
                    )
            }
        )
    }

    private fun getUserIdHeaderValueForLoginApi(mobileNumber: String, countryCallingCode: String): String {
        return "+$countryCallingCode-$mobileNumber"
    }

    companion object {
        private const val GENERATE_UID_SUB_URL = "chaukidar/v1/generate/uid"
        private const val SEND_OTP_SUB_URL = "chaukidar/v1/otp"
        private const val RESEND_OTP_SUB_URL = "chaukidar/v1/resend-otp"
        private const val VERIFY_LOGIN_SUB_URL = "chaukidar/v1/app/login"
        private const val REFRESH_TOKEN_SUB_URL = "chaukidar/v1/refreshTokens"
        private const val LOGOUT_USER_SUB_URL = "/chaukidar/v1/logout"

        private const val USER_ID = "userId"
    }
}
