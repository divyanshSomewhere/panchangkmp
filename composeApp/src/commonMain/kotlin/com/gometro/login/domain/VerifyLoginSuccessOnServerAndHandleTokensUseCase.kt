package com.gometro.login.domain

import com.gometro.base.featurecontracts.Device
import com.gometro.login.constants.LoginFeatureErrorCodes
import com.gometro.login.data.errors.LoginRemoteErrorCodes
import com.gometro.login.data.exceptions.LoginVerificationFailedException
import com.gometro.login.data.exceptions.ProfileAndTokensExceptions
import com.gometro.login.data.models.app.LoginModeAppModel
import com.gometro.login.data.models.app.PostLoginAuthTokensAppModel
import com.gometro.login.data.repository.LoginRepository
import com.gometro.network.exception.ApiCallLocalNetworkException
import com.gometro.network.exception.NetworkSuccessResponseParseException

class VerifyLoginSuccessOnServerAndHandleTokensUseCase(
    private val loginRepository: LoginRepository,
    private val parseAndStoreTokensUseCase: ParseAndStoreTokensUseCase,
    private val device: Device
) {

    suspend operator fun invoke(loginModeAppModel: LoginModeAppModel): LoginVerificationResult {
        val result = try {
            loginRepository.verifyLoginSuccessOnServerAndGetTokens(
                deviceId = device.getDeviceId(),
                loginModeAppModel = loginModeAppModel
            )
        } catch (e: LoginVerificationFailedException) {
            return when (e.genericApiErrorResponse?.errorCode) {
                LoginRemoteErrorCodes.LOGIN_INVALID_OTP_ENTERED -> LoginVerificationResult.ServerError.InvalidOtpEntered
                LoginRemoteErrorCodes.LOGIN_INTERNAL_SERVER_ERROR -> LoginVerificationResult.ServerError.UnknownError(e.genericApiErrorResponse.message)
                else -> LoginVerificationResult.ServerError.UnknownError(e.genericApiErrorResponse?.message)
            }
        } catch (e: ApiCallLocalNetworkException) {
            return LoginVerificationResult.LocalError(e.message)
        } catch (e: NetworkSuccessResponseParseException) {
            return LoginVerificationResult.ServerError.ParseError
        } catch (e: ProfileAndTokensExceptions) {
            return when (e) {
                is ProfileAndTokensExceptions.InvalidAccessToken,
                is ProfileAndTokensExceptions.InvalidRefreshToken -> {
                    LoginVerificationResult.ServerError.InvalidTokensReceived
                }
                is ProfileAndTokensExceptions.InvalidProfileDetails -> {
                    LoginVerificationResult.ServerError.InvalidProfileReceived
                }
            }
        }

        val postLoginAuthTokensAppModel = PostLoginAuthTokensAppModel(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken
        )
        val tokensParsedAndStoredSuccessfully = parseAndStoreTokensUseCase.invoke(postLoginAuthTokensAppModel)

        return if (tokensParsedAndStoredSuccessfully) {
            loginRepository.storeUserProfileDetails(result.userProfile)
            LoginVerificationResult.LoginVerified
        } else {
            LoginVerificationResult.TokenProcessingError
        }
    }
}

sealed class LoginVerificationResult {
    object LoginVerified : LoginVerificationResult()

    data class LocalError(val errorMsg: String?) : LoginVerificationResult()
    sealed class ServerError : LoginVerificationResult() {
        object InvalidOtpEntered : ServerError()
        data class UnknownError(val errorMsg: String?) : ServerError()
        object InvalidProfileReceived : ServerError()
        object InvalidTokensReceived : ServerError()
        object ParseError : ServerError()
    }
    object TokenProcessingError : LoginVerificationResult()

    fun getErrorCodes(): Int {
        return when (this) {
            LoginVerified -> -1 // will not happen, because not an error case
            ServerError.InvalidTokensReceived -> LoginFeatureErrorCodes.LOGIN_VERIFY_INVALID_TOKENS_RECEIVED
            is LocalError -> LoginFeatureErrorCodes.LOGIN_VERIFY_LOCAL_ERROR
            ServerError.InvalidOtpEntered -> LoginFeatureErrorCodes.LOGIN_VERIFY_INVALID_OTP
            is ServerError.UnknownError -> LoginFeatureErrorCodes.LOGIN_VERIFY_UNKNOWN_ERROR
            TokenProcessingError -> LoginFeatureErrorCodes.LOGIN_VERIFY_TOKEN_PROCESSING_ERROR
            ServerError.InvalidProfileReceived -> LoginFeatureErrorCodes.LOGIN_VERIFY_INVALID_PROFILE
            ServerError.ParseError -> LoginFeatureErrorCodes.LOGIN_VERIFY_PARSE_ERROR
        }
    }
}
